import { CurrencyRupee, Discount, EditOutlined } from '@mui/icons-material';
import KeyIcon from '@mui/icons-material/Key';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import { Alert, Box, Button, Card, CardContent, Container, Grid, Rating, Snackbar, Tab, Tabs, Typography } from '@mui/material';
import { Carousel, Image } from 'antd';
import axios from 'axios';
import { format } from 'date-fns';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useCallback, useEffect, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { OptionMenu } from 'src/layouts/option-menu';
import { host, itemImage, toTitleCase } from 'src/utils/util';
import ReportIcon from '@mui/icons-material/Report';
import CommentIcon from '@mui/icons-material/Comment';
import {ItemReports} from 'src/sections/wholesale/item-reports';
import { ItemReviews } from 'src/sections/wholesale/item-reviews';




function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`full-width-tabpanel-${index}`}
      aria-labelledby={`full-width-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box sx={{ p: 3 }}>
          {children}
        </Box>
      )}
    </div>
  );
}
const Page = () => {

  // For snackbar
  const [open,setOpen] = useState()
  const [message, setMessage] = useState("")
  const [flag, setFlag] = useState("warning")

  const auth = useAuth()
  const router = useRouter()
  const {slug}  = router.query
  const [item,setItem] = useState({})
  const itemCreatedAt =   format(!!item.createdAt ? item.createdAt : 0, 'dd/MM/yyyy')
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const [itemReviews,setItemReviews] = useState([])
  const [itemReports,setItemReports] = useState([])
  const [currentTarget,setCurrentTarget] = useState(null)

  // filter data
  const [data,setData] = useState({
    pageNumber : page,
    size : rowsPerPage
  })

  const [totalReviewsElements , setTotalReviwesElements] = useState(0)
  const [totalReportsElements , setTotalReportsElements] = useState(0)
  const [value, setValue] = useState(0);

  // Getting item details
  useEffect(() => {
    const getData = async () => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        await axios.get(host + "/admin/item/detail/"+slug,)
            .then(res => {
                const result = res.data.res;
                setItem(result)
                setData({...data, itemId : result.id,})
            })
            .catch(err => {
                setMessage(!!err.response ? err.response.data.message : err.message)
                setFlag('error')
                setOpen(true)
            })
    }
    getData();

}, [])


// Getting item reviews */
useEffect( ()=>{
    const getData = async () => {
       axios.defaults.headers = {
         Authorization : auth.token
       }
       await axios.post(host+"/admin/item/review/all",{...data,itemId : item.id})
       .then(res => {
          const data = res.data;
           setItemReviews(data.content);
           setTotalReviwesElements(data.totalElements)
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



// Getting item reports */
useEffect( ()=>{
  const getData = async () => {
     axios.defaults.headers = {
       Authorization : auth.token
     }
     await axios.post(host+"/admin/item/report/all",data)
     .then(res => {
        const data = res.data;
         setItemReports(data.content);
         setTotalReportsElements(data.totalElements)
         console.log("item reports : "+JSON.stringify(data.content))
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



  function a11yProps(index) {
    return {
      id: `full-width-tab-${index}`,
      'aria-controls': `full-width-tabpanel-${index}`,
    };
  }

  const handleChange = (event,newValue) => {
    setValue(newValue)
  } 


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
        <Container maxWidth="xxl">
          <Card>
            <CardContent>
              <Grid container>
                  <Grid item xs={12} md={3}> 

                    {/* image carousel */}
                      <Carousel style={{
                          width : '100%',
                          height : 'auto',
                          maxHeight : 400,
                          background : '#303030'
                        }}>
                            {!!item.avtars && item.avtars.split(',').map((avtar,i) =>{
                            return (<Image
                                  key={i}
                                  src={itemImage+item.slug+"/"+avtar}
                                  height={400}
                                  width={'100%'}
                                  alt={item.name}
                                  style={{objectFit : 'contain'}} 
                              />)
                            })}
                        </Carousel>
                  </Grid>

                  {/* item Detail */}
                    <Grid item xs={12} md={6} 
                          sx={{
                            display : 'flex',
                            flexDirection : 'column',
                            justifyContent : 'center',
                            px : 5
                          }}
                      >
            
                      <Box sx={{ textAlign : 'left',}}>
                          <Typography component="div" variant="h5">
                              {toTitleCase(item.name)}
                          </Typography>
                          <Typography
                              variant="h6"
                              component="div"
                              sx={{ color: 'text.secondary',fontSize : 15, my:1 }}
                          >
                              <div style={{
                              display: 'flex',
                              alignItems: 'center',
                              flexWrap: 'wrap',
                              }}>
                              {/* <AccessTimeIcon sx={{ mr: 1 , p:0.1 }} /> */}
                              Created at : {itemCreatedAt}
                              </div>  
                  
                          </Typography>

                          <Typography
                              variant="h6"
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
                              variant="h6"
                              component="div"
                              sx={{ color: 'text.secondary',fontSize : 15, my:1 }}
                          >
                              <div style={{
                              display: 'flex',
                              alignItems: 'center',
                              flexWrap: 'wrap',
                              }}>
                              <CommentIcon sx={{ mr: 1, padding: 0.2 }} />
                              <span>{(item.totalComments)} Comments</span>
                              </div>  
                          </Typography>

                          <Typography
                              variant="h6"
                              component="div"
                              sx={{ color: 'text.secondary',fontSize : 15, my:1 }}
                          >
                              <div style={{
                              display: 'flex',
                              alignItems: 'center',
                              flexWrap: 'wrap',
                              }}>
                              <ReportIcon sx={{ mr: 1, padding: 0.2 }} />
                              <span>{(item.totalReportsCount)} Reports </span>
                              </div>  
                          </Typography>


                          <Typography
                              variant="h6"
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
                                    
                                      <div>Current Pice : <span style={{ fontWeight:'bold' , fontSize : '20px', marginRight : '10px' }}> 
                                        { (Math.round((item.price - item.discount) * 100) / 100).toFixed(2)}</span></div>
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
                                        display: 'flex'
                                      }}>
                                      <div style={{display:'flex'}}>
                                          <strike style={{fontSize : 14}}>{(Math.round((item.price) * 100) / 100).toFixed(2)}</strike>
                                          <CurrencyRupee sx={{ padding: 0.3, mr: 1 }}/>
                                      </div>
                                      <div style={{display:'flex'}}>
                                          <span style={{ color:'red',fontSize : 14}}>
                                            {(Math.round((item.discount) * 100) / 100).toFixed(2)}
                                            </span>
                                          <CurrencyRupee sx={{ padding: 0.3, mr: 1 }}/>
                                          <Discount sx={{ padding: 0.3, mr: 1 }}/>
                                      </div>
                                      </div>  
                                  </Grid>
                              </Grid>
                          </Typography>

                        <Box sx={{display : 'flex', alignItems : 'center',my:1}}>
                          <Rating readOnly value={parseFloat(item.rating)} sx={{mx : 1}}/>
                            <Typography>
                                  {item.totalRatingCount} Ratings
                            </Typography>
                        </Box>
                      
                          <Typography
                                  variant="h6"
                                  component="div"
                                  sx={{ color: 'text.secondary', fontSize: 15, mt: 1 }}
                              >
                          
                                  <div style={{
                                      display: 'flex',
                                      alignItems: 'center',
                                      flexWrap: 'wrap',
                                      }}>
                                      <span style={{ color: "green" ,  fontSize : 15}}>Currently {item.inStock == "Y" ? <span style={{color:'green'}}>Avilable</span> : <span>Unavilable</span>}</span>
                                  </div>  
                          </Typography>
                        </Box>
                  </Grid>


                 <Grid item xs={12} md={2} 
                  style={{
                    display : "flex",
                    justifyContent : 'flex-end',
                    alignItems :'center'
                  }}
                 >
                        <Link href = {{
                            pathname : "/item/update/[slug]",
                            query : {slug : slug}
                        }}
                          style={{ 
                            textDecoration : 'none', 
                            color:'#6C737F',
                          }}
                        >
                          <Button variant='outlined' icon> <Typography>
                            Edit
                          </Typography> </Button>
                      </Link>         
                </Grid>

              </Grid>
            </CardContent>
          </Card>

          {/* Tabs */}
          <Box
            sx={{
              display : 'flex',
              justifyContent : 'center',
              alignItems : 'center',
              mt : 5
            }}
          >
              <Tabs  
                  value={value}
                  onChange={handleChange}
                  aria-label="icon tabs example"
                >
                <Tab icon={<CommentIcon />} aria-label="comments" {...a11yProps(0)}  label="COMMENTS" />
                <Tab icon={<ReportIcon />} aria-label="comments"  {...a11yProps(1)} label= "REPORTS"  />
              </Tabs>
          </Box>

            {/* Comments and reviews */}
            <TabPanel value={value} index={0}>
             {/* Item Reviews */}
             <Box>
                <ItemReviews 
                  count={totalReviewsElements}
                  itemReviews={itemReviews}
                  onPageChange={handlePageChange}
                  onRowsPerPageChange={handleRowsPerPageChange}
                  page={page}
                  rowsPerPage={rowsPerPage}

                />
              </Box>
            </TabPanel>

            <TabPanel value={value} index={1}>
            {/* Item Reports */}
              <Box>
                <ItemReports 
                  count={totalReportsElements}
                  itemReports={itemReports}
                  onPageChange={handlePageChange}
                  onRowsPerPageChange={handleRowsPerPageChange}
                  page={page}
                  rowsPerPage={rowsPerPage}

                />
              </Box>
            </TabPanel>
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
