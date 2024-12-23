
import { Avatar, Box, Button, Grid, Link, Typography } from '@mui/material'
import bg from 'public/assets/bg.png'
import HomeNavbar from 'src/sections/top-nav'
import { host, projectName } from 'src/utils/util'
import { CheckCircleOutline, CheckOutlined } from '@mui/icons-material'
import { useEffect, useState } from 'react'
import axios from 'axios'
import {useRouter } from 'next/router'
import { redirect } from 'next/dist/server/api-utils'


function Pricing() {

    const amount = 99.00;
    const router = useRouter()

    const handlePayment = (e,amount)=>{
        e.preventDefault()
        axios.post(host+"/pg/pay" , {amount : amount})
        .then(res => {
            let url = res.data.url
            console.log(url);
          
        })
        .catch(err => console.log(err))
    }

    const redirectForPayment = (amount) =>{
        window.open(host+"/pg/pay/"+amount)
    }


  return (<Box
        sx={{
            backgroundImage:`url(${bg.src})`,
            backgroundRepeat: "no-repeat",
            backgroundSize: "cover",
            height : '100vh'
        }}
     >
        <HomeNavbar />


        <Grid md={10} sx={{m :'0 auto'}} container>
        <Grid xs={12} md={12} sx={{
                    textAlign : 'center',
                    my : 10,
                    mt : 20
                }} >
            
                <Box>
                    <Typography variant='h3'>
                        Starter Plans For Your Online Store
                    </Typography>
                </Box>
                    
            </Grid>


            {[1,2,3].map(i => {
            return (
                <Grid key={i} xs={12} md={4} sx={{
                    px : 2,
                }} >
                    <Box sx={{
                            borderRadius : 2,
                            background : 'white',
                            height : 850,
                            boxShadow : 6,
                            border : '4px solid #6366f1',
                            px : 3,
                            mb : 5
                        }}>
                           <Box >
                                 <Box sx={{mt : 2}}>
                                    <Typography variant='h3'>
                                        {projectName} Trial
                                    </Typography>
                                </Box>   
                         

                                    <Box sx={{my : 3}}>
                                         
                                        <Typography variant='h6'>
                                            ₹{amount} <span>/month</span>
                                          
                                          </Typography>

                                          <Typography variant='p'>
                                            <Link 
                                            style={{
                                                textDecoration : 'none'
                                            }}
                                            href="/welcome"
                                            > 1 Month Trial</Link>
                                          </Typography>
                                    </Box>

                                    <Box sx={{
                                        display : 'flex',
                                        flexDirection : 'column'
                                    }}>
                                        <Typography variant='span'>
                                            *GST extra
                                        </Typography>
                                        <Button 
                                            sx={{width : 200, my : 1}} 
                                            variant="outlined" 
                                            type='button' 
                                            onClick={(e)=>redirectForPayment(amount)} 
                                            >Get Trial Plan
                                        </Button>
                                        <a rel="prefetch" href={host+"/pg/pay/"+amount}>Hello</a>
                           
                                    <Box sx={{my:1}}>
                                        <Typography variant='h6'>Advanced Features</Typography>                                    
                                        <Box sx={{ml : 2}}>
                                            {[1,2,3,4,5,6].map(i =>{
                                                return (
                                                    <Box sx={{
                                                            my : 1,
                                                            display : 'flex',
                                                            alignItems : 'center',
                                                            textAlign : 'center'
                                                        }}>
                                                        <CheckCircleOutline sx={{color : 'green',mr : 1}}/>
                                                        <Typography variant='p'>
                                                            Al Reply/Compose
                                                        </Typography>
                                                    </Box>
                                                )
                                            })}
                                        </Box>         
                                    </Box>                        
                                </Box>
                            </Box>
                        </Box>
                </Grid>
            )
         })}   
   
       
        </Grid>
     </Box>
  )
}

export default Pricing