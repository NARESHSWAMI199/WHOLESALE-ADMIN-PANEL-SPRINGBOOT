import { useCallback, useEffect, useMemo, useState } from 'react';
import Head from 'next/head';
import {  Alert, Box,Container, Snackbar, Stack, SvgIcon, Typography, useMediaQuery } from '@mui/material';
import { useSelection } from 'src/hooks/use-selection';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { BasicSearch, CustomersSearch } from 'src/sections/basic-search';
import axios from 'axios';
import { host } from 'src/utils/util';
import { useAuth } from 'src/hooks/use-auth';
import { CustomerHeaders } from 'src/sections/customer/customers-header';
import { StoresCard } from 'src/sections/wholesale/stores-table';
import { Divider, Pagination } from 'antd';
import { useRouter } from 'next/router';
import Spinner from 'src/sections/spinner';



const now = new Date();

const Page = () => {

  const [open,setOpen] = useState()
  const [message, setMessage] = useState("")
  const [flag, setFlag] = useState("warning")

  const auth = useAuth()
  const [totalPages,setTotalPages] = useState(0)

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [stores,setStores] = useState([])
  const [showSpinner , setShowSpinner] = useState("block")

  
  const [data,setData] = useState({
    pageNumber : page,
    size : rowsPerPage
  })

  const [totalElements , setTotalElements] = useState(0)




  useEffect( ()=>{
    const getData = async () => {
       axios.defaults.headers = {
         Authorization : auth.token
       }
       await axios.post(host+"/admin/store/all",data)
       .then(res => {
          const response = res.data;
          setTotalElements(response.totalElements)
          setStores(response.content);
          setTotalPages(response.totalPages)
          setShowSpinner("none")
       })
       .catch(err => {
         setFlag("error")
         setMessage(!!err.response ? err.response.data.message : err.message)
         setOpen(true)
         setShowSpinner("none")
       } )
     }
    getData();

   },[data])


  

  const udpateDeltedStore = (slug)=>{
    setStores((stores)=> stores.filter((storeItem) => storeItem.slug !==slug ) )
  }


  
  const onDelete = (slug) => {
    axios.defaults.headers = {
      Authorization :  auth.token  
    }
    axios.get(host+`/admin/store/delete/${slug}`)
    .then(res => {
        setFlag("success")
        setMessage(res.data.message)
        setOpen(true)
        udpateDeltedStore(slug)
    }).catch(err => {
      console.log(err)
      setMessage(!!err.response ? err.response.data.message : err.message)
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
      setData({...data, pageNumber : value})
    },
    []
  );

  const handleRowsPerPageChange = useCallback(
    (event) => {
      setRowsPerPage(event.target.value);
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
          py: 8,
        }}
      >
        <Box sx={{
                    margin : '0 auto',
                    width : '95%',
                    overflow : 'auto'
                }}>
          <Stack spacing={3}>
            <CustomerHeaders  headerTitle={"All Store"} userType="W" />
            <BasicSearch onSearch={onSearch} />
          <Spinner show={showSpinner}/>
          {stores.map((store,i) =>{
             return(<StoresCard  key={i} 
              deleteStore={onDelete}
              store={store}  />)
          } ) }

      {/* <CardActions sx={{ justifyContent: 'flex-end' }}>
        <Button
          onClick={getMore}
          color="inherit"
          endIcon={(
            <SvgIcon fontSize="small">
              <ArrowRightIcon />
            </SvgIcon>
          )}
          size="small"
          variant="text"
        >
          View more
        </Button>
      </CardActions> */}
                <Box sx={{m:2,display:'flex',justifyContent:'center'}}>
                    <Pagination  onChange={(page) => setData({...data,pageNumber : page-1})}  align="center" defaultCurrent={1} total={totalPages*10} />
               </Box>
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
