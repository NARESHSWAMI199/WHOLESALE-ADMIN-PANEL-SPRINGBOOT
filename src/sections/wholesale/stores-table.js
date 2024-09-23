import * as React from 'react';
import { useTheme } from '@mui/material/styles';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CardMedia from '@mui/material/CardMedia';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import SkipPreviousIcon from '@mui/icons-material/SkipPrevious';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import SkipNextIcon from '@mui/icons-material/SkipNext';
import { Rating } from '@mui/material';
import { Button } from 'antd';
import { bgcolor, color } from '@mui/system';
import { bg } from 'date-fns/locale';
import { CheckCircleOutlined, DeleteFilled, EditFilled } from '@ant-design/icons';
import { CheckOutlined, Edit } from '@mui/icons-material';
import { useState } from 'react';
import { useEffect } from 'react';

import { format } from 'date-fns';

export const StoresCard = (props) => {
  const theme = useTheme();
  const [store , setStore] = useState(props.store)
  const createdAt =   format(!!store.createdAt ? store.createdAt : 0, 'dd/MM/yyyy')
  const [message , setMessage] = useState("")
  const [slug , setSlug] = useState("")
  const [status , setStatus] = useState(store.status)
  
      

  useEffect(()=>{
    setStore(props.store)
    console.log(store)
  },[props])

  const toTitleCase = (str) => {
    if (!!str)
    return str.replace(
      /\w\S*/g,
      text => text.charAt(0).toUpperCase() + text.substring(1).toLowerCase()
    );
    return "____"
  }



  /**actions */

  const updateStatus = (slug,status) =>{
    props.updateStatus(slug,status)
  }


  
  const deleteStore = (status) =>{
    props.deleteStore(status)
  }


   
  const editStore = (status) =>{
    props.editStore(status)
  }






  return (
    <Card sx={{ display: 'flex', paddingRight : 5 }}>
        {/* Wholesale image */}
        <CardMedia
            component="img"
            sx={{ width: 200 }}
            image={store.avtar}
            alt="Live from space album cover"
      />
      <Box sx={{ display: 'flex', flexDirection: 'column' }}>
        <CardContent sx={{ flex: '1 0 auto', mx : '20px' }}>
          <Typography component="div" variant="h5">
            {toTitleCase(store.storeName)}
          </Typography>
          <Typography
            variant="subtitle"
            component="div"
            sx={{ color: 'text.secondary',fontSize : 15, my:1 }}
          >
           Registered Date : {createdAt}
          </Typography>

          <Typography
            variant="subtitle"
            component="div"
            sx={{ color: 'text.secondary',fontSize : 15, my:1 }}
          >
            Token : <span style={{color:"green"}}>{store.slug}</span>
          </Typography>


          <Typography
            variant="subtitle"
            component="div"
            sx={{ color: 'text.primary',fontSize : 15, my:1 }}
          >
            Contact Number : {store.phone} ||  Email Id : {store.email}
          </Typography>

          <Rating value={store.rating} sx={{my:1}}/>
        </CardContent>
      </Box>
      <Box sx={{ display: 'flex', flexDirection: 'column', my : 4 , ml : 'auto'}}>

        
        { status !== 'A' ?
        <Button  type='primary' variant="outlined" icon={<CheckCircleOutlined />} style={{background:'#5cb85c'}} onClick={(e)=> {
                          setMessage("We are going to activate this store.")
                          updateStatus(store.slug,"A")
                          setStatus("A")
                        }} >
            Active
        </Button>
        :
        <Button  type='primary' variant="outlined" icon={<CheckCircleOutlined />} onClick={(e)=> {
                          setMessage("We are going to deactivate this store.")
                          updateStatus(store.slug , "D")
                          setStatus("D")
                        }} style={{background:'#ffc107', color : "black"}}>
            Deactive
        </Button>
      } 
        <Button type='primary'  style= {{marginTop : '5px'}}    icon={<EditFilled />} primary>
            Edit
        </Button>
        <Button type="primary" variant="outlined" style= {{marginTop : '5px'}} icon={<DeleteFilled />} danger >
            Delete
        </Button>
      </Box>
    </Card>
  );
}