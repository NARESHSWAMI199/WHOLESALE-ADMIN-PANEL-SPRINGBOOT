import { ArrowRightAltOutlined } from '@mui/icons-material'
import { Avatar, Box, Button, SvgIcon, Typography } from '@mui/material'
import Link from 'next/link'
import bg from 'public/assets/bg.png'
import logo from 'public/assets/logos/logo.png'
import HomeNavbar from 'src/sections/top-nav'
function Page() {

  return (
    <Box
        sx={{
            backgroundImage:`url(${bg.src})`,
            backgroundRepeat: "no-repeat",
            backgroundSize: "cover",
            // background : 'red',
            height : '100vh'
        }}
     >
    <HomeNavbar/>
    <Box 
        sx={{
            display : 'flex',
            justifyContent : 'center',
            alignItems : 'center',
            height : '80%',
            flexDirection : 'column'
        }}
    >
    <Box sx={{
        display :'flex',
        alignItems : 'center',
        justifyContent :'center',
        height : 150,
        width : 150,
        background : 'white',
        borderRadius : 50,
        boxShadow : 1,
        my : 5
    }}> 


        <Avatar  sx={{
            height : 100,
            width : 150,
            objectFit :'contain',

        }} src={logo.src} /> 
    </Box>
        <Typography 
            variant='h1'
            sx={{
                fontFamily : 'Georgia, serif',
                fontSize : 100
            }}
        ><span style={{fontSize : 120}}>W</span>elcome to <span style={{color : '#6366f1',fontSize : 120}}>S</span>wami <span style={{color : '#6366f1',fontSize : 120}}>S</span>ales
        </Typography>

        <Typography 
            variant='h3'
            sx={{
                fontFamily : 'serif'
            }}
        >
         Grow your bussinus with swami sales.
        </Typography>
        <Typography 
            variant='p'
            sx={{
                fontFamily : 'serif'
            }}
        > Hey ! It's time to switch online.
        </Typography>


        <Link href={{
            pathname : "/pricing"
        }} style={{
                height : 60,
                width: 300,
                fontWeight : 'bold',
                fontSize : 18,
                my : 10,
                background : 'white',
                color : 'black',
                position : 'absolute',
                bottom : 170,
                right : 200,
                borderRadius : 20,
                textDecoration : 'none',
                display : 'flex',
                justifyContent : 'center',
                alignItems : 'center',
            }} > 
            <Box sx={{
                display : 'flex',
                justifyContent : 'center',
                alignItems : 'center',
                }}>
                Register Now / Try Now
                <ArrowRightAltOutlined sx={{fontWeight : 'bold', mx : 1}}/>
            </Box>
          </Link>
    </Box>
   </Box>
  )
}


export default Page

