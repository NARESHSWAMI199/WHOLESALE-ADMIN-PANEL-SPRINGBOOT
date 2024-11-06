import { useCallback, useEffect, useMemo, useState } from 'react';
import Head from 'next/head';
import {  Alert, Box, Button, Container, Snackbar, Stack, SvgIcon, Typography } from '@mui/material';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { applyPagination } from 'src/utils/apply-pagination';
import axios, { all } from 'axios';
import { host } from 'src/utils/util';
import { useAuth } from 'src/hooks/use-auth';
import { BasicHeaders } from 'src/sections/basic-header';
import Link from 'next/link';
import PlusIcon from '@heroicons/react/24/solid/PlusIcon';
import { CategoryTable } from 'src/sections/category/categoryTable';



/** STORE CATEGORIES */


const Page = () => {


  /** snackbar varibatles */

  const [open,setOpen] = useState()
  const [message, setMessage] = useState("")
  const [flag, setFlag] = useState("warning")


  const auth = useAuth()
const [categories,setCategories] = useState([])


  useEffect( ()=>{
    const getData = async () => {
       axios.defaults.headers = {
         Authorization : auth.token
       }
       await axios.post(host+"/admin/store/category",{})
       .then(res => {
          const data = res.data;
           setCategories(data);
       })
       .catch(err => {
         setMessage(err.message)
         setFlag("error")
         setMessage(!!err.response ? err.response.data.message : err.message)
         setOpen(true)
       } )
     }
    getData();

   },[])



   const onDelete = (categorySlug,rowIndex) => {
    axios.defaults.headers = {
      Authorization :  auth.token  
    }
    axios.get(host+`/admin/store/category/delete/${categorySlug}`)
    .then(res => {
        setFlag("success")
        setMessage(res.data.message)
        setOpen(true)
        setCategories((categories) =>categories.filter((_, index) => index !== rowIndex));
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
          <Box sx={{
                    margin : '0 auto',
                    width : '95%'
                }}>
          <Stack spacing={3}>
                   
          <Stack
              direction="row"
              justifyContent="space-between"
              spacing={4}
            >
          <BasicHeaders  headerTitle={"All Store Categories"}/>
          <div>
              <Link
                    href={{
                        pathname: '/store/category/create',
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
            <CategoryTable
              editUrl = "/store/category/"
              items={categories}
              onDelete = {onDelete}
            />
          </Stack>
        </Box>
        
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
