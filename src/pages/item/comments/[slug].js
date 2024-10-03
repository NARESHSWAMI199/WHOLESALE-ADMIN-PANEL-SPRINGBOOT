import { useCallback, useEffect, useMemo, useState } from 'react';
import Head from 'next/head';
import ArrowDownOnSquareIcon from '@heroicons/react/24/solid/ArrowDownOnSquareIcon';
import ArrowUpOnSquareIcon from '@heroicons/react/24/solid/ArrowUpOnSquareIcon';
import PlusIcon from '@heroicons/react/24/solid/PlusIcon';
import {  Alert, Box, Button, CardActions, CardContent, Container, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Grid, Rating, Snackbar, Stack, SvgIcon, Typography, useMediaQuery } from '@mui/material';
import { useSelection } from 'src/hooks/use-selection';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { CustomersTable } from 'src/sections/customer/customers-table';
import { BasicSearch, CustomersSearch } from 'src/sections/basic-search';
import { applyPagination } from 'src/utils/apply-pagination';
import axios from 'axios';
import { host, itemImage, toTitleCase } from 'src/utils/util';
import { useAuth } from 'src/hooks/use-auth';
import { CustomerHeaders } from 'src/sections/customer/customers-header';
import { StoresCard } from 'src/sections/wholesale/stores-table';
import { Divider, Image } from 'antd';
import { ArrowRightIcon } from '@mui/x-date-pickers';
import { useRouter } from 'next/router';
import { CurrencyRupee, Discount, DiscountOutlined, KeyOutlined } from '@mui/icons-material';
import EmailOutlinedIcon from '@mui/icons-material/EmailOutlined';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import KeyIcon from '@mui/icons-material/Key';
import PersonIcon from '@mui/icons-material/Person';
import { format } from 'date-fns';
import { fontSize } from '@mui/system';



const now = new Date();

const Page = () => {

  const [open,setOpen] = useState()
  const [message, setMessage] = useState("")
  const [flag, setFlag] = useState("warning")

  const auth = useAuth()
  const router = useRouter()
  const {slug}  = router.query
  const [item,setItem] = useState({})
  const createdAt =   format(!!item.createdAt ? item.createdAt : 0, 'dd/MM/yyyy')
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [comments,setComments] = useState([])
  
  const [data,setData] = useState({
    itemId : item.id,
    pageNumber : page,
    size : rowsPerPage
  })

  const [totalElements , setTotalElements] = useState(0)



  useEffect(() => {
    const getData = async () => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        await axios.get(host + "/admin/item/detail/"+slug,)
            .then(res => {
                const data = res.data.res;
                setItem(data)
            })
            .catch(err => {
                setMessage(!!err.response ? err.response.data.message : err.message)
                setFlag('error')
                setOpen(true)
            })
    }
    getData();

}, [])

/** for item comments */
useEffect( ()=>{
    const getData = async () => {
       axios.defaults.headers = {
         Authorization : auth.token
       }
       await axios.post(host+"/admin/item/comments/all",data)
       .then(res => {
          const data = res.data;
           setComments(data);
           console.log(data)
       })
       .catch(err => {
         setFlag("error")
         setMessage(!!err.response ? err.response.data.message : err.message)
         setOpen(true)
       } )
     }
    getData();

   },[data])


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
          Wholesaler | Swami Sales
        </title>
      </Head>
    {/* Box using for div */}
      <Box
        component="main"
        sx={{
            flexGrow: 1,
            py: 8
          }}
      >
        {/* Container using for responsiveness */}
        <Container maxWidth="xl">
            {/* <BasicSearch onSearch={onSearch} /> */}
            <Grid container spacing={3}>
                <Grid xs={12} md={4} sx={{alignContent : 'center'}}> 
                    <Image
                        width={450}
                        height={350}
                        src={itemImage+item.avtar}
                    />
                </Grid>
                 {/* item Detail */}
                <Grid item xs={12} md={6}>
                    
                        <CardContent sx={{ flex: '1 0 auto', mx : '20px' }}>
                        <Typography component="div" variant="h5">
                            {toTitleCase(item.name)}
                        </Typography>
                        <Typography
                            variant="subtitle"
                            component="div"
                            sx={{ color: 'text.secondary',fontSize : 20, my:1 }}
                        >
                            <div style={{
                            display: 'flex',
                            alignItems: 'center',
                            flexWrap: 'wrap',
                            }}>
                            <AccessTimeIcon sx={{ mr: 1 , p:0.1 }} />
                            Created at : {createdAt}
                            </div>  
                
                        </Typography>

                        <Typography
                            variant="subtitle"
                            component="div"
                            sx={{ color: 'text.secondary',fontSize : 15, my:1 }}
                        >
                            <div style={{
                            display: 'flex',
                            alignItems: 'center',
                            flexWrap: 'wrap',
                            }}>
                            <KeyIcon sx={{ mr: 1, padding: 0.2 }} />
                            <span style={{ color: "green" }}>{item.slug}</span>
                            </div>  
                        </Typography>


                        <Typography
                            variant="subtitle"
                            component="div"
                            sx={{ color: 'text.secondary', fontSize: 15, my: 1 }}
                        >
                 
                            <Grid container spacing={2}>
                                <Grid item xs={6} md={6}>
                                    <div style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    flexWrap: 'wrap',
                                    textDecoration : 'none'
                                    }}>
                                   
                                    <div>Current Pice : <span style={{ fontWeight:'bold' , fontSize : '20px', marginRight : '10px' }}> { (Math.round((item.price - item.discount) * 100) / 100).toFixed(2)}</span></div>
                                    <CurrencyRupee sx={{ padding: 0.3, mr: 1}}/>
                                    </div>  
                                </Grid>
                             </Grid>
                             </Typography>

                            <Typography
                                variant="subtitle"
                                component="div"
                                sx={{ color: 'text.secondary', fontSize: 15, my: 1 }}
                            >
                            <Grid container spacing={2}>
                                <Grid item xs={6} md={6}>
                                    <div style={{
                                    display: 'flex',
                                    // alignItems: 'center',
                                    // flexWrap: 'wrap',
                                    // textDecoration : 'none'
                                    }}>
                                    <div style={{display:'flex'}}>
                                        <strike style={{fontSize : 20}}>{(Math.round((item.price) * 100) / 100).toFixed(2)}</strike>
                                        <CurrencyRupee sx={{ padding: 0.3, mr: 1 }}/>
                                    </div>
                                    <div style={{display:'flex'}}>
                                        <span style={{ color:'red',fontSize : 20}}>{(Math.round((item.discount) * 100) / 100).toFixed(2)}</span>
                                        <Discount sx={{ padding: 0.3, mr: 1 }}/>
                                    </div>
                                    </div>  
                                </Grid>
                            </Grid>
                        </Typography>

                        <Rating value={item.rating} sx={{my:1}}/>

                        <Typography
                                variant="subtitle"
                                component="div"
                                sx={{ color: 'text.secondary', fontSize: 15, my: 1 }}
                            >
                        
                                <div style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    flexWrap: 'wrap',
                                    }}>
                                    <span style={{ color: "green" ,  fontSize : 15}}>Currently {item.inStock == "Y" ? <span style={{color:'green'}}>Avilable</span> : <span>Unavilable</span>}</span>
                                 </div>  
                        </Typography>
                 
                        </CardContent>
          
                </Grid>
            </Grid>
        </Container>


        {comments.map(comment => {
            return (<div style={{width : '100%' , height:'100px'}}>
                {comment.message}
                <span>{comment.createdAt}</span>
            </div>)
        })}

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
