import { useEffect, useState } from 'react';
import Head from 'next/head';
import {  Alert, Box, Button, Container, Snackbar, Stack, SvgIcon } from '@mui/material';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import axios from 'axios';
import { host } from 'src/utils/util';
import { useAuth } from 'src/hooks/use-auth';
import { BasicHeaders } from 'src/sections/basic-header';
import Link from 'next/link';
import PlusIcon from '@heroicons/react/24/solid/PlusIcon';
import { CategoryTable } from 'src/sections/category/categoryTable';


/** ITEM CATEGORY */

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
       await axios.post(host+"/admin/item/category",{})
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
    axios.post(`${host}/admin/item/category/delete`,{
      "slug" : categorySlug
    })
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
          <Container maxWidth="xxl" sx={{
              px : {
                      xs : 0,
                      sm : 0,
                      md : 0,
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
          <BasicHeaders  headerTitle={"All Items Categories"}/>
          <div>
              <Link
                   href={{
                    pathname: '/item/category/create',
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
              editUrl = {'/item/category/'}
              items={categories}
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
