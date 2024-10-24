
import { useCallback, useEffect, useMemo, useState } from 'react';
import Head from 'next/head';
import {  Alert, Box, Button, CardActions, Checkbox, Container, FormControl, FormControlLabel, FormGroup, FormHelperText, FormLabel, Grid, InputAdornment, Link, OutlinedInput, Snackbar, Stack, SvgIcon, Typography } from '@mui/material';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import MagnifyingGlassIcon from '@heroicons/react/24/solid/MagnifyingGlassIcon';
import WorkspacePremiumIcon from '@mui/icons-material/WorkspacePremium';
import { BasicHeaders } from 'src/sections/basic-header';
import axios, { all } from 'axios';
import { useAuth } from 'src/hooks/use-auth';
import { host } from 'src/utils/util';
import { useRouter } from 'next/router';

const Page = ()=> {

  const [flag,setFlag] = useState('warning')
  const [open,setOpen] = useState(false)
  const [message,setMessage] = useState("")
  const [permissions, setPermissions] = useState({})
  const [group , setGroup] = useState({})
  const [givenPermissions, setGivenPermissions] = useState([])
  let permissionsIds = []
  const auth = useAuth();
  const router = useRouter()
  const {userSlug} = router.query


  useEffect(() => {
    axios.defaults.headers = {
      Authorization: auth.token
    }

    // Get all permmission 
    axios.get(host + "/admin/auth/wholesale/permissions/"+userSlug)
      .then(res => {
        let allPermissions = res.data.allPermissions;
        setPermissions(allPermissions)
        let assigned = res.data.assigned;
        setGivenPermissions(assigned)
      })
      .catch(err => {
        setFlag("error")
        setMessage(!!err.response  ? err.response.data.message : err.message)
        setOpen(true)
      })
      // end here.
  }, [])



  const createGroup = (event) =>{
    event.preventDefault()
    let data = {
      userType : "W",
      slug : userSlug,
      storePermissions : givenPermissions

    }
    axios.defaults.headers = {
      Authorization: auth.token
    }
    axios.post(host + "/admin/auth/wholesaler/permissions/update",data)
      .then(res => {
        let response = res.data;
        console.log(response)
        setMessage(response.message)
        setFlag("success")
        setOpen(true)
      })
      .catch(err => {
        setMessage(!!err.response  ? err.response.data.message : err.message)
        setFlag("error")
        setOpen(true)
      })
  }



  const allowAll = (event) =>{
    let isChecked = event.target.checked
    let allPermission = []
    if(isChecked){
      Object.keys(permissions).map((key)=>{
        //allPermission = [...allPermission, ...permissions[key].permission]
        (permissions[key].map((permission) => allPermission.push(permission.id))
      )})
      setGivenPermissions(allPermission)
      //setGivenPermissions([...(permissions.permissions)])
    }else{
      //setGivenPermissions([...(permissions.permissions)])
      setGivenPermissions([])
    }


    //console.log(givenPermissions)
  }

  const changeName = (event) =>{
    setGroup((perviouse) => ({...perviouse, group : event.target.value}))
  }

  const handleChange = (event) => {

    const permissionId =  event.target.name
    let isChecked =  event.target.checked
 if(isChecked){
      setGivenPermissions((perviouse)=>[...perviouse,parseInt(permissionId)])
    }else {
      setGivenPermissions((perviouse)=>perviouse.filter((item)=> item != parseInt(permissionId)))
    } 
  };


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
          Store Permissions | Swami Sales
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
                    
          
          
          <form onSubmit={(e)=>createGroup(e)}>
          <Stack spacing={3}>
          <BasicHeaders  headerTitle={"Edit Store Permissions"}/>
          <Grid spacing={3}>
                <Grid xs={2}>
                  <FormControlLabel sx={{mx:5}}
                    control={
                      <Checkbox checked={givenPermissions.length > 0} indeterminate={givenPermissions.length > 0}  onChange={allowAll} name={0} />
                    }
                    label={"All Permissions"}/>
                </Grid>
              </Grid>
          {/* permissions */}
          <Grid container spacing={3}>
                {Object.keys(permissions).map((element,index) => {
                return (<Grid xs={3}
                  key={index} >
                  <FormControl sx={{ m: 3 }} component="fieldset" variant="standard">
                    <FormLabel component="legend">{element}</FormLabel>
                    {<FormGroup>
                      {permissions[element].map((item,i)=>{
                       return( <FormControlLabel
                        key={i}
                          control={
                            <Checkbox checked={givenPermissions.includes(item.id)} onChange={handleChange} name={item.id} />
                          }
                         label={item.permission}
                        />)
                      })}
                    </FormGroup>}
                </FormControl>
                </Grid>)
                })}        
               
            </Grid>
              {/* end here... */}
 
              <CardActions sx={{ justifyContent: 'flex-end' }}>
                    <Button type="submit" variant="contained">
                      Save details
                    </Button>
              </CardActions>
            


            </Stack>
            </form>
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