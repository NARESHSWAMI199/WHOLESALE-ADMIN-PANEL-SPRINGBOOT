
import { useCallback, useEffect, useMemo, useState } from 'react';
import Head from 'next/head';
import {  Alert, Box, Button, Checkbox, Container, FormControl, FormControlLabel, FormGroup, FormHelperText, FormLabel, Grid, InputAdornment, OutlinedInput, Snackbar, Stack, SvgIcon, Typography } from '@mui/material';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import MagnifyingGlassIcon from '@heroicons/react/24/solid/MagnifyingGlassIcon';
import KeyIcon from '@mui/icons-material/Key';
import WorkspacePremiumIcon from '@mui/icons-material/WorkspacePremium';
import { GroupHeaders } from 'src/sections/group-header';
import { BasicHeaders } from 'src/sections/basic-header';

const Page = ()=> {
  const [state, setState] = useState({
    gilad: true,
    jason: false,
    antoine: false,
  });


  const [flag,setFlag] = useState('warning')
  const [open,setOpen] = useState(false)
  const [message,setMessage] = useState("")
  const [permissions, setPermissions] = useState([{}])


  useEffect(() => {
    axios.defaults.headers = {
      Authorization: auth.token
    }
    axios.get(host + "/group/permissions/all/")
      .then(res => {
        let response = res.data;
        setPermissions(response)
      })
      .catch(err => console.log(err))
  }, [slug])




  const handleChange = (event) => {
    setState({
      ...state,
      [event.target.name]: event.target.checked,
    });
  };

const { gilad, jason, antoine } = state;
const error = [gilad, jason, antoine].filter((v) => v).length !== 2;

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
          <BasicHeaders  headerTitle={"Group Permissions"}/>

            <OutlinedInput
                    defaultValue=""
                    fullWidth
                    placeholder="Group Name"
                    name='slug'
                    startAdornment={(
                    <InputAdornment position="start" >
                        
                        <WorkspacePremiumIcon
                        color="action"
                        fontSize="small"
                        >
                        <MagnifyingGlassIcon />
                        </WorkspacePremiumIcon>
                    </InputAdornment>
                    )}
                    sx={{ maxWidth: 540 }}
                />

<Box sx={{ display: 'flex' }}>
                <FormControl sx={{ m: 3 }} component="fieldset" variant="standard">
                    <FormLabel component="legend">Permissions</FormLabel>
                    <FormGroup>
                    <FormControlLabel
                        control={
                        <Checkbox checked={gilad} onChange={handleChange} name="gilad" />
                        }
                        label="Store List"
                    />


                    <FormControlLabel
                        control={
                        <Checkbox checked={gilad} onChange={handleChange} name="gilad" />
                        }
                        label="Store Edit"
                    />


                    <FormControlLabel
                        control={
                        <Checkbox checked={gilad} onChange={handleChange} name="gilad" />
                        }
                        label="Store Status"
                    />

                    <FormControlLabel
                        control={
                        <Checkbox checked={gilad} onChange={handleChange} name="gilad" />
                        }
                        label="Store Delete"
                    />

                    </FormGroup>
                </FormControl>



                <FormControl sx={{ m: 3 }} component="fieldset" variant="standard">
                    <FormLabel component="legend">Users</FormLabel>
                    <FormGroup>
                    <FormControlLabel
                        control={
                        <Checkbox checked={gilad} onChange={handleChange} name="gilad" />
                        }
                        label="User List"
                    />


                    <FormControlLabel
                        control={
                        <Checkbox checked={gilad} onChange={handleChange} name="gilad" />
                        }
                        label="User Edit"
                    />


                    <FormControlLabel
                        control={
                        <Checkbox checked={gilad} onChange={handleChange} name="gilad" />
                        }
                        label="User Status"
                    />

                    <FormControlLabel
                        control={
                        <Checkbox checked={gilad} onChange={handleChange} name="gilad" />
                        }
                        label="User Delete"
                    />

                    </FormGroup>
                </FormControl>


               
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
