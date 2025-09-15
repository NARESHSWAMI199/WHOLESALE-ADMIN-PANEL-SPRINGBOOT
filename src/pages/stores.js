import { Alert, Box, Container, Snackbar, Stack } from '@mui/material';
import { Pagination } from 'antd';
import axios from 'axios';
import Head from 'next/head';
import { useCallback, useEffect, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { BasicSearch } from 'src/sections/basic-search';
import { CustomerHeaders } from 'src/sections/customer/customers-header';
import Spinner from 'src/sections/spinner';
import { StoresCard } from 'src/sections/wholesale/stores-table';
import { host, rowsPerPageOptions } from 'src/utils/util';

const Page = () => {

  const [open,setOpen] = useState()
  const [message, setMessage] = useState("")
  const [flag, setFlag] = useState("warning")

  const auth = useAuth()
  const paginations = auth.paginations
  const [totalPages,setTotalPages] = useState(0)

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(paginations?.STORES?.rowsNumber);
  const [stores,setStores] = useState([])
  const [showSpinner , setShowSpinner] = useState("block")

  
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
    axios.post(`${host}/admin/store/delete`,{
      slug : slug
    })
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
          Wholesaler | Swami Sales
        </title>
      </Head>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          py: 5,
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
              <CustomerHeaders  headerTitle={"All Store"} userType="W" />
              <BasicSearch onSearch={onSearch} />
            <Spinner show={showSpinner}/>
            {stores.map((store,i) =>{
              return(<StoresCard  key={i} 
                deleteStore={onDelete}
                store={store}  />)
            } ) }

                  <Box sx={{m:2,display:'flex',justifyContent:'center'}}>
                    {/* total take page * 10 like if 4 page then write 40 */}
                      <Pagination  onChange={(page) => setData({
                          ...data,
                          pageNumber : page-1
                          }
                        )}  align="center" defaultCurrent={1} total={totalPages*10} />  
                </Box>
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
