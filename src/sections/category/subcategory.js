import { Box, Button, Card, CardActions, CardContent, CardHeader, Container, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Divider, Grid, Stack, SvgIcon, TextField, useMediaQuery } from '@mui/material';
import React, { useCallback, useEffect, useState } from 'react'
import ImageInput from '../image-input';
import { CropSquareSharp, DeleteOutline } from '@mui/icons-material';
import CloseIcon from '@mui/icons-material/Close';
import { useTheme } from '@emotion/react';

var categoryIcon = null;
const SubcategoryCard = (props) => {

const [values,setValues] = useState(!!props.subcategory ? props.subcategory : {})
const [message,setMessage] = useState("")
const [confirm,setConfirm] = useState(false)
const [slug,setSlug] = useState(null)
const [action,setAction] = useState('')
const theme = useTheme();
const fullScreen = useMediaQuery(theme.breakpoints.down('md'));

useEffect(()=>{
  if(!!props.subcategory)
  setValues(props.subcategory)
},[props.subcategory])

  const handleChange = useCallback(
    (event) => {
      setValues((prevState) => ({
        ...prevState,
        [event.target.name]: event.target.value
      }));
    },
    []
  );

  const takeAction = (action) =>{
    props.onDelete(slug)
    setConfirm(false)
  }
  
  const handleClose =  () =>{
    setConfirm(false)
}
const confirmBox = () => {
  setConfirm(true)
};


  const handleSubmit = useCallback(
    (e) =>{
    e.preventDefault()
    const form = e.target;
    const formData = new FormData(form)
    let data = {
        id : values.id,
        categoryId :  props.categoryId,
        subcategory : formData.get("subcategory"),
        icon : categoryIcon !=null ? categoryIcon : values.icon
        }
        props.onSubmit(data)
        categoryIcon = null
    })


const generateThumbnail = (file) => {
    if(!file) return false
    let reader = new FileReader();
    reader.onload = (event) => {
        const base64Data = event.target.result;
        categoryIcon = base64Data
    }
    reader.readAsDataURL(file);
    
}

const onSubmit = (image) =>{
  if(!!image)
  generateThumbnail(image.originFileObj)
}
return (<>
<Card sx={{
  width : '100%',
}}>
    <form autoComplete="off" onSubmit={handleSubmit}>
      <Box sx={{display : 'flex', flexDirection : 'row', flex : 1}}>
        <Box>
          <CardHeader
              title= {props.buttonLabel ==null || props.buttonLabel == undefined ? "Add Subcategory" : "Edit Subcategory"}
          />
        </Box>
        <Box sx={{ml : 'auto', padding : 2, paddingTop : 4 }} 
          onClick={(e)=>{
            setSlug(values.slug)
            setMessage("We are going to delete this subcategory. if you agree press agree otherwise press disagree.")
            setAction("delete")
            confirmBox()
          }}>
          <SvgIcon>
            <CloseIcon/>
          </SvgIcon>
        </Box>
        </Box>
    <CardContent>
    <Box>
    <Grid xs={12} md={12} container spacing={3} style={{
      margin : 'auto'
    }}>
        <Grid
            xs={12}
            md={12}
            sx={{
              marginBottom : 3
            }}
            >
          <Box>
              <ImageInput onChange={onSubmit} avtar =  {values.icon }/>
          </Box>
        </Grid>
        <Grid
            xs={12}
            md={12}
            >
            <TextField
                fullWidth
                label="Subcategory"
                name="subcategory"
                onChange={handleChange}
                required={true}
                value={values.subcategory }
            />
            </Grid>
        </Grid>
    </Box>
    </CardContent>
      <CardActions sx={{ justifyContent: 'center' }}>
      <Button style={{width : '100%'}} type="submit" variant="contained">
          {props.buttonLabel ==null || props.buttonLabel == undefined ? "Save" : props.buttonLabel}
      </Button>
      </CardActions>
        </form>
    </Card>


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
        <Button onClick={()=>takeAction(action)} autoFocus>
          Agree
        </Button>
      </DialogActions>
      </Dialog>
</>
  )
}

export default SubcategoryCard