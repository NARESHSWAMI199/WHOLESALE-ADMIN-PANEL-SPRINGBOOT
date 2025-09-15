import { useCallback, useEffect, useMemo, useState } from 'react';
import Head from 'next/head';
import {  Alert, Box, Button, Container, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Snackbar, Stack, SvgIcon, Typography } from '@mui/material';
import { useSelection } from 'src/hooks/use-selection';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { CustomersTable } from 'src/sections/customer/customers-table';
import { BasicSearch, CustomersSearch } from 'src/sections/basic-search';
import { applyPagination } from 'src/utils/apply-pagination';
import axios, { all } from 'axios';
import { host, rowsPerPageOptions } from 'src/utils/util';
import { useAuth } from 'src/hooks/use-auth';
import MagnifyingGlassIcon from '@heroicons/react/24/solid/MagnifyingGlassIcon';
import { Card, InputAdornment, OutlinedInput } from '@mui/material';
import { CustomerHeaders } from 'src/sections/customer/customers-header';
import { GroupTable } from 'src/sections/groups/groups-table';
import { BasicHeaders } from 'src/sections/basic-header';
import Link from 'next/link';
import PlusIcon from '@heroicons/react/24/solid/PlusIcon';
import { set } from 'nprogress';






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
  const paginations = auth.paginations;
  const [error,setErrors] = useState("")
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(paginations?.GROUPS?.rowsNumber);
  const [groups,setGroups] = useState([])
  const groupsIds = useCustomerIds(groups);
  const groupsSelection = useSelection(groupsIds);
  const [deleted,setDeleted] = useState(false);
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
       await axios.post(host+"/group/all",data)
       .then(res => {
          const data = res.data.content;
           setTotalElements(res.data.totalElements)
           setGroups(data);
       })
       .catch(err => {
         setErrors(err.message)
         setFlag("error")
         setMessage(!!err.response ? err.response.data.message : err.message)
         setOpen(true)
       } )
     }
    getData();

   },[data])



  
  const onDelete = (slug,rowIndex) => {
    axios.defaults.headers = {
      Authorization :  auth.token  
    }
    axios.post(`${host}/group/delete`,{
      slug : slug
    })
    .then(res => {
        setFlag("success")
        setMessage(res.data.message)
        setDeleted(true)
        setOpen(true)
        setGroups((items) =>items.filter((_, index) => index !== rowIndex));
    }).catch(err => {
      console.log(err)
      setFlag("error")
      setMessage(!!err.response ? err.response.data.message : err.message)
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
      setData((perviouse) => ({...perviouse,pageNumber : value}))
    },
    []
  );

  const handleRowsPerPageChange = useCallback(
    (event) => {
      setRowsPerPage(event.target.value);
      setData((perviouse) => ({...perviouse,size : event.target.value}))
    },
    []
  );


  const onSearch = (searchData) => {
    if(!!searchData){
    setData({
      ...data,
      ...searchData,
      pageNumber : 0 // when search reset the page number
    })
    setPage(0)
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
          Group Permissions | Swami Sales
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
                   
          <Stack
              direction="row"
              justifyContent="space-between"
              spacing={4}
            >
          <BasicHeaders  headerTitle={"All Groups"}/>
          <div>
              <Link
                    href={{
                        pathname: '/group/create',
                      }}
                    >
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
                </Link>
              </div>
            </Stack>
          <BasicSearch  onSearch={onSearch} type="G" />

            <GroupTable
              count={totalElements}
              items={groups}
              onDeselectAll={groupsSelection.handleDeselectAll}
              onDeselectOne={groupsSelection.handleDeselectOne}
              onPageChange={handlePageChange}
              onRowsPerPageChange={handleRowsPerPageChange}
              onSelectAll={groupsSelection.handleSelectAll}
              onSelectOne={groupsSelection.handleSelectOne}
              page={page}
              rowsPerPage={rowsPerPage}
              selected={groupsSelection.selected}
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
