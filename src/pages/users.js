import { Alert, Box, Container, Snackbar, Stack } from '@mui/material';
import axios from 'axios';
import Head from 'next/head';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';
import { useSelection } from 'src/hooks/use-selection';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { BasicSearch } from 'src/sections/basic-search';
import { CustomerHeaders } from 'src/sections/customer/customers-header';
import { CustomersTable } from 'src/sections/customer/customers-table';
import { applyPagination } from 'src/utils/apply-pagination';
import { host, rowsPerPageOptions } from 'src/utils/util';







const now = new Date();


const useCustomers = (content,page,rowsPerPage) => {
  return useMemo(
    () => {
      return applyPagination(content, page, rowsPerPage);
    },
    [page, rowsPerPage]
  )
};

const useCustomerIds = (customers) => {
  return useMemo(
    () => {
      return customers.map((customer) => customer.id);
    },
    [customers]
  );
};


const Page = () => {


  /** snackbar varibatles */

  const [open,setOpen] = useState()
  const [message, setMessage] = useState("")
  const [flag, setFlag] = useState("warning")


  const auth = useAuth()
  const paginations = auth.paginations
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(paginations?.USERS?.rowsNumber);
  const [customers,setCustomers] = useState([])
  const customersIds = useCustomerIds(customers);
  const customersSelection = useSelection(customersIds);
  const [data,setData] = useState({
    pageNumber : page,
    size : !!rowsPerPage ? rowsPerPage : rowsPerPageOptions[0]
  })

  const [totalElements , setTotalElements] = useState(0)

  useEffect( ()=>{
    const getData = async () => {
       axios.defaults.headers = {
         Authorization : auth.token
       }
       await axios.post(`${host}/admin/auth/${!!data?.userType ? data.userType : 'A'}/all`,data)
       .then(res => {
          const response = res.data.content;
           setTotalElements(res.data.totalElements)
           setCustomers(response);
       })
       .catch(err => {
         setFlag("error")
         setMessage(!!err.response ? err.response.data.message : err.message)
         setOpen(true)
       } )
     }
    getData();

   },[data,page,rowsPerPage])

   const updateStatusOnUi = (status,slug) =>{
    setCustomers((items) => {
      items.filter((_, index) => {
        if(_.slug === slug) return _.status = status
        return _;
      })
      return items
    });
  }

  const onStatusChange = (slug,status) => {
    axios.defaults.headers = {
      Authorization :  auth.token  
    }
    axios.post(host+`/admin/auth/status`,{
      slug : slug,
      status : status
    })
    .then(res => {
      if (status === "A") {
        setFlag("success")
        setMessage("Successfully activated.")
      }else {
        setFlag("warning")
        setMessage("Successfully deactivated.")
      }
      setOpen(true)
      updateStatusOnUi(status,slug)
    }).catch(err => {
      console.log(err)
      setFlag("error")
      setMessage(!!err.response ? err.response.data.message : err.message)
      setOpen(true)
    } )
  }
  

  
  const onDelete = (slug) => {
    axios.defaults.headers = {
      Authorization :  auth.token  
    }
    axios.post(`${host}/admin/auth/delete`,{
      slug : slug
    })
    .then(res => {
        setFlag("success")
        setMessage(res.data.message)
        setOpen(true)
        setCustomers((items) =>items.filter((item) => item.slug !== slug));
    }).catch(err => {
      console.log(err)
      setMessage(err.message)
      setFlag("error")
      setOpen(true)
    } )
  }
  

  /** for snackbar close */
  const handleClose = () => {
    setOpen(false)
  };


  const handlePageChange = useCallback(
    (event, value) => {
      setPage(value);
      setData((perviouse) => ({...perviouse, pageNumber : value}))
    },
    []
  );

  const handleRowsPerPageChange = useCallback(
    (event) => {
      setRowsPerPage(event.target.value);
      setData((perviouse) => ({...perviouse,size :event.target.value}))
    },
    []
  );

  const onSearch = (searchData) => {
    if(!!searchData){
    setData({
      ...data,
      ...searchData,
    })
  }else {
    setData({
      pageNumber : page,
      size : rowsPerPage
    })
  }
  } 
  

  return (
    <>

    <Snackbar anchorOrigin={{ vertical : 'top', horizontal : 'right' }}
        open={open}
        onClose={handleClose}
        key={'top' + 'right'}
      >
     <Alert onClose={handleClose} severity={flag} sx={{ width: '100%' }}>
        {message}
    </Alert>
    </Snackbar>
      <Head>
        <title>
          Wholesaler | Swami Sales
        </title>
      </Head>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          py: 8
        }}
      >
          <Container maxWidth="xxl" sx={{
                    px : {
                            xs : 1,
                            sm : 1,
                            md : 1,
                            lg : 5,
                            xl : 5
                        } 
            }}>
            <Stack spacing={3}>
              <CustomerHeaders  headerTitle={"All users"} userType="R" /> {/* User type R for retailers when you click on add button we go form userType Retailer by default. */}
              <BasicSearch onSearch={onSearch} type="A" />

            <CustomersTable
                count={totalElements}
                items={customers}
                onDeselectAll={customersSelection.handleDeselectAll}
                onDeselectOne={customersSelection.handleDeselectOne}
                onPageChange={handlePageChange}
                onRowsPerPageChange={handleRowsPerPageChange}
                onSelectAll={customersSelection.handleSelectAll}
                onSelectOne={customersSelection.handleSelectOne}
                page={page}
                rowsPerPage={rowsPerPage}
                selected={customersSelection.selected}
                onStatusChange = {onStatusChange}
                onDelete = {onDelete}
              />
            </Stack>
        </Container>
      </Box>
    </>
  );
};

Page.getLayout = (page) => (
  <DashboardLayout>
    {page}
  </DashboardLayout>
);

export default Page;
