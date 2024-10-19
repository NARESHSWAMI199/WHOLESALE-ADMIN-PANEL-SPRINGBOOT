import { Box, Button, Card, CardActions, CardContent, CardHeader, Container, Divider, Grid, Stack, TextField } from '@mui/material';
import React, { useCallback, useState } from 'react'
import ImageInput from '../image-input';


var categoryIcon = null;
const SubcategoryCard = (props) => {

const [values,setValues] = useState(!!props.subcategory ? props.subcategory : {})

  const handleChange = useCallback(
    (event) => {
      setValues((prevState) => ({
        ...prevState,
        [event.target.name]: event.target.value
      }));
    },
    []
  );
  


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
    })


const generateThumbnail = (file) => {
    if(!file) return false
    let reader = new FileReader();
    reader.onload = (event) => {
        const base64Data = event.target.result;
        categoryIcon = base64Data
        console.log(categoryIcon)
    }
    reader.readAsDataURL(file);
    
}

const onSubmit = (image) =>{
  if(!!image)
  generateThumbnail(image.originFileObj)
}
return (<Card sx={{
  width : '100%',
}}>
    <form autoComplete="off" onSubmit={handleSubmit}>
      <CardHeader
          title="Add Subcategory"
      />
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
              <ImageInput onChange={onSubmit} avtar = {values.icon}/>
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
  )
}

export default SubcategoryCard