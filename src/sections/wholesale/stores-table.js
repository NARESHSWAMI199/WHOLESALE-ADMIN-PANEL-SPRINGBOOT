import { CheckCircleOutlined, DeleteFilled, EditFilled, RightSquareFilled } from '@ant-design/icons';
import styled from '@emotion/styled';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import EmailOutlinedIcon from '@mui/icons-material/EmailOutlined';
import KeyIcon from '@mui/icons-material/Key';
import LocalPhoneIcon from '@mui/icons-material/LocalPhone';
import PersonIcon from '@mui/icons-material/Person';
import ShoppingCartCheckoutIcon from '@mui/icons-material/ShoppingCartCheckout';
import { Alert, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Grid, Rating, Snackbar, useMediaQuery } from '@mui/material';
import Box from '@mui/material/Box';
import CardContent from '@mui/material/CardContent';
import { useTheme } from '@mui/material/styles';
import Typography from '@mui/material/Typography';
import { Button } from 'antd';
import axios from 'axios';
import { format } from 'date-fns';
import Link from 'next/link';
import { set } from 'nprogress';
import { useEffect, useMemo, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';
import { host, storeImage, toTitleCase } from 'src/utils/util';


export const StoresCard = (props) => {
  const {showVisitButton} = props
  const [store , setStore] = useState(props.store)
  const createdAt =   format(!!store.createdAt ? store.createdAt : 0, 'dd/MM/yyyy')
  const [message , setMessage] = useState("")
  const [slug , setSlug] = useState(store.slug)
  const [status , setStatus] = useState(store.status)
  const [confirm , setConfirm] = useState(false)
  const [action, setAction] = useState("")

  const [flag, setFlag] = useState("warning")
  const [open,setOpen] = useState()
  
  const auth = useAuth()
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('md'));


  useEffect(()=>{
    setStore(props.store)
    setStatus(store.status)
    setSlug(store.slug)
  },[props])

  const updateStatus = (slug,status) => {
    axios.defaults.headers = {
      Authorization :  auth.token  
    }
    axios.post(host+`/admin/store/status`,{
      slug : slug,
      status : status
    })
    .then(res => {
      if (status === "A") {
        setFlag("success")
        setMessage("Successfully activated.")
        setStatus("A")
      }else {
        setFlag("warning")
        setMessage("Successfully deactivated.")
        setStatus("D")
      }
      setOpen(true)
      setStatus(status)
    }).catch(err => {
      console.log(err)
      setFlag("error")
      setMessage(!!err.response ? err.response.data.message : err.message)
      setOpen(true)
    } )
  }

  const confirmBox = (message,action,slug) =>{
    setTimeout(()=>{
      setSlug(slug)
      setAction(action)
      setMessage(message)
      setConfirm(true) // delay to avoid multiple dialog open issue
    },100)
    setOpen(false)
  }

  const takeAction = () =>{
      if(action === 'delete'){
        props.deleteStore(slug)
      }else if(action === 'status'){
        updateStatus(slug, status === 'A' ? 'D' : 'A')
      }
      setConfirm(false)
  }

  const handleClose = () =>{
      setConfirm(false)
  }


  const ImageContainer =useMemo(()=>{
    return styled.div`
      display: flex;
      justify-content: center;
      align-items: center;
  `;
  },[])

  const Image = useMemo(()=>{ 
    return styled.img`
    max-width: 100%;
    height: auto;
    object-fit : cover; 

    @media (min-width: 768px) {
      width: 360px;
    }
  `;
  },[])

  return (<>
  <Grid container sx={{boxShadow : 1 , borderRadius : 1}}>
      
          <Grid xs={12} md={2} 
          sx={{
            display : 'flex' , 
            justifyContent : 'center' , 
            alignItems : 'center'
          }}
          >
          <ImageContainer>
              <Image
                  src= {storeImage+store.slug+"/"+store.avtar}
                  alt="Live from space store cover"
            />
           </ImageContainer>
          </Grid>

          <Grid xs={12} md={8} sx={{
                display : 'flex' , 
                flexDirection: 'column', 
                justifyContent : 'center' , 
                textAlign : 'left'
              }} >
              <CardContent sx={{ flex: '1 0 auto', mx : '20px' ,
                display : 'flex',
                flexDirection : 'column',
                justifyContent : 'center',
                textAlign : 'left' 
                }}>
                <Typography component="div" variant="h5">
                  <Link title='Store Detail' style={{textDecoration : 'none' , color : 'black'}} href={"/store/"+ store.user?.slug}>
                    {toTitleCase(store.storeName)}
                  </Link>
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
                    <span style={{ color: "green" }}>{store.slug}</span>
                  </div>  
                </Typography>



                <Typography
                  variant="subtitle"
                  component="div"
                  sx={{ color: 'text.secondary',fontSize : 15, my:1}}
                >
                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    flexWrap: 'wrap',
                  }}>
                
                    <PersonIcon sx={{ mr: 1 }} />
                    <Link style={{textDecoration : 'none' , color : '#6C737F'}} href={"/wholesalers/"+store.user?.slug}>
                    <span title='Check user Detail' >{toTitleCase(store.user?.username)}</span>
                    </Link>
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
                    <LocalPhoneIcon sx={{padding : 0.3 , mr:1}}/>
                    <span> {store.phone}</span>
                  </div>  
                </Typography>


                <Typography
                  variant="subtitle"
                  component="div"
                  sx={{ color: 'text.secondary', fontSize: 15, my: 1 }}
                >
                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    flexWrap: 'wrap',
                    textDecoration : 'none'
                  }}>
                    <EmailOutlinedIcon sx={{ padding: 0.3, mr: 1 }}/>
                      <Link href={"mailto:" + store.email} style={{ textDecoration: 'none' }}> {store.email}</Link>

                  </div>  
                </Typography>

                <Typography
                  variant="subtitle"
                  component="div"
                  sx={{ color: 'text.secondary', fontSize: 15, my: 1 }}
                >
                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    flexWrap: 'wrap',
                    textDecoration : 'none',
                    color : 'green'
                  }}>
                    <ShoppingCartCheckoutIcon sx={{ padding: 0.3, mr: 1 }}/>
                       {store.totalStoreItems}

                  </div>  
                </Typography>

                <Rating readOnly={true} value={store.rating} sx={{my:1}}/>
              </CardContent>
          </Grid>


          <Grid xs={12} md={1.2} >
              <Box sx={{ display: 'flex', flexDirection: 'column', height : '100%', justifyContent : 'center'}}>
                { status !== 'A' ?
                <Button  type='primary' variant="outlined" icon={<CheckCircleOutlined />} style={{background:'#5cb85c'}} onClick={(e)=> {
                                  confirmBox("We are going to activate this store.","status",store.slug)
                                }} >
                    Active
                </Button>
                :
                <Button  type='primary' variant="outlined" icon={<CheckCircleOutlined />} onClick={(e)=> {
                                  confirmBox("We are going to deactivate this store.","status",store.slug)
                                }} style={{background:'#ffc107', color : "black"}}>
                    Deactive
                </Button>
              } 
              <Link
                    href={{
                      pathname: '/store/update/[slug]',
                      query: { slug: store.slug },
                    }}
                  >
                  <Button type='primary'  style= {{marginTop : '5px',width:'100%'}}  icon={<EditFilled />} primary>
                      Edit
                  </Button>
                </Link>


                <Link title='Store Detail' style={{textDecoration : 'none' , color : 'black'}} href={"/store/"+ store.user?.slug}>
                { showVisitButton !=false &&
                <Button type='primary'  style= {{marginTop : '5px',width:'100%'}}  icon={<RightSquareFilled />} primary>
                      Visit {confirm + ""}
                  </Button>
    }
                </Link>
                <Button type="primary" variant="outlined" style= {{marginTop : '5px'}} icon={<DeleteFilled />} danger 
                  onClick={(e) =>{
                    confirmBox(`Are you sure you want delete store ${store.name}`,"delete",store.slug)
                  }}

                >
                    Delete
                </Button>
              </Box>
          </Grid>

    <Dialog
      fullScreen={fullScreen}
      open={confirm}
      onClose={handleClose}
      aria-labelledby="responsive-dialog-title"
      >
      <DialogTitle id="responsive-dialog-title">
        {"Are you sure ?"}
      </DialogTitle>
      <DialogContent>
        <DialogContentText>
        {message}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button autoFocus onClick={handleClose}>
          Disagree
        </Button>
        <Button onClick={()=>takeAction()} autoFocus>
          Agree
        </Button>
      </DialogActions>
    </Dialog>




      <Snackbar anchorOrigin={{ vertical : 'top', horizontal : 'right' }}
          open={open}
          onClose={handleClose}
          key={'top' + 'right'}
        >
      <Alert onClose={handleClose} severity={flag} sx={{ width: '100%' }}>
          {message}
      </Alert>
      </Snackbar>
  
    </Grid>
</>
  );
}