import { useCallback, useEffect, useMemo, useState } from 'react';
import Head from 'next/head';
import ArrowDownOnSquareIcon from '@heroicons/react/24/solid/ArrowDownOnSquareIcon';
import ArrowUpOnSquareIcon from '@heroicons/react/24/solid/ArrowUpOnSquareIcon';
import PlusIcon from '@heroicons/react/24/solid/PlusIcon';
import {  Alert, Box, Button, Container, Grid, Snackbar, Stack, SvgIcon, Typography } from '@mui/material';
import { useSelection } from 'src/hooks/use-selection';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { CustomersTable } from 'src/sections/customer/customers-table';
import { CustomersSearch } from 'src/sections/customer/customers-search';
import { applyPagination } from 'src/utils/apply-pagination';
import axios, { all } from 'axios';
import { host } from 'src/utils/util';
import { useAuth } from 'src/hooks/use-auth';
import MagnifyingGlassIcon from '@heroicons/react/24/solid/MagnifyingGlassIcon';
import { Card, InputAdornment, OutlinedInput } from '@mui/material';






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
  let [status,setStatus] = useState(null)
  const [error,setErrors] = useState("")
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [customers,setCustomers] = useState([])
  const customersIds = useCustomerIds(customers);
  const customersSelection = useSelection(customersIds);
  const [data,setData] = useState({
    userType : "R",
    pageNumber : page,
    size : rowsPerPage
  })

  const [totalElements , setTotalElements] = useState(0)

  useEffect( ()=>{
    const getData = async () => {
       axios.defaults.headers = {
         Authorization : auth.token
       }
       await axios.post(host+"/admin/auth/all",data)
       .then(res => {
          const data = res.data.content;
           setTotalElements(res.data.totalElements)
           setCustomers(data);
       })
       .catch(err => {
         setErrors(err.message)
       } )
     }
    getData();

   },[data])



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
      setStatus(status)
    }).catch(err => {
      console.log(err)
    } )
  }
  

  
  const onDelete = (slug) => {
    axios.defaults.headers = {
      Authorization :  auth.token  
    }
    axios.get(host+`/admin/auth/delete/${slug}`)
    .then(res => {
        setFlag("success")
        setMessage(res.data.message)
      setOpen(true)
      setStatus(status)
    }).catch(err => {
      console.log(err)
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

  const onSearch = (value) => {
    setData({
      ...data,
      searchKey : value
    })
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
          Retailer | Swami Sales
        </title>
      </Head>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          py: 8
        }}
      >
        <Container maxWidth="xl">
          <Stack spacing={3}>
            <Stack
              direction="row"
              justifyContent="space-between"
              spacing={4}
            >
              <Stack spacing={1}>
                <Typography variant="h4">
                  Retailer
                </Typography>
                <Stack
                  alignItems="center"
                  direction="row"
                  spacing={1}
                >
                  <Button
                    color="inherit"
                    startIcon={(
                      <SvgIcon fontSize="small">
                        <ArrowUpOnSquareIcon />
                      </SvgIcon>
                    )}
                  >
                    Import
                  </Button>
                  <Button
                    color="inherit"
                    startIcon={(
                      <SvgIcon fontSize="small">
                        <ArrowDownOnSquareIcon />
                      </SvgIcon>
                    )}
                  >
                    Export
                  </Button>
                </Stack>
              </Stack>
              <div>
                <Button
                  startIcon={(
                    <SvgIcon fontSize="small">
                      <PlusIcon />
                    </SvgIcon>
                  )}
                  variant="contained"
                >
                  Add
                </Button>
              </div>
            </Stack>

            <CustomersSearch  onSearch={onSearch} />

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
