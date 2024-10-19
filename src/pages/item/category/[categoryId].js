import { Box,
  Button, 
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Divider,
  MenuItem,
  Select,
  TextField,
  Unstable_Grid2 as Grid,
  InputLabel,
  FormControl,
  Snackbar,
  Checkbox,
  Alert,
  Container,
  Stack,
  SvgIcon,
  cardContentClasses
} from "@mui/material";
import axios from "axios";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "src/hooks/use-auth";
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { host } from "src/utils/util";
import { useRouter } from "next/router";
import ImageInput from "src/sections/image-input";
import { Typography } from "antd";
import PlusIcon from "@heroicons/react/24/solid/PlusIcon";
import Link from "next/link";
import SubcategoryCard from "src/sections/category/subcategory";
import { margin, style, width } from "@mui/system";


const createItem = () =>{    

const router = useRouter()
const {categoryId} = router.query
/** WE CAN'T USE STATE FOR BASE 64 LARGE DATA STATES WILL NOT WORK FOR LARGE DATA*/


const [open,setOpen] = useState(false)
const [message,setMessage] = useState("")
const [flag,setFlag] = useState("success")

const auth = useAuth()
// const [values,setValues] = useState({})
const [category,setCategory] = useState({})
const [subcategories,setSubcategories] = useState([])
const [subcategoryCard,setSubcategoriesCard] = useState();
const [values,setValues] = useState([])



useEffect(() => {
  const getData = async () => {
      axios.defaults.headers = {
          Authorization: auth.token
      }
      await axios.get(host + "/admin/item/category/"+categoryId)
          .then(res => {
              const data = res.data;
              setCategory(data)
          })
          .catch(err => {
              setMessage(!!err.response ? err.response.data.message : err.message)
              setFlag('error')
              setOpen(true)
          })
  }
  getData();

}, [])



useEffect(() => {
  const getAllSubcategories = async () => {
      axios.defaults.headers = {
          Authorization: auth.token
      }
      await axios.get(host + "/admin/item/subcategory/"+categoryId)
          .then(res => {
              const data = res.data;
              setSubcategories(data)
              setValues(data)
          })
          .catch(err => {
              setMessage(!!err.response ? err.response.data.message : err.message)
              setFlag('error')
              setOpen(true)
          })
  }
  getAllSubcategories();

}, [])



const handleChange = useCallback(
  (event) => {
    setValues((prevState) => ({
      ...prevState,
      [event.target.name]: event.target.value
    }));
  },
  []
);

const handleClose = useCallback(()=>{
      setOpen(false)
})

const addSubCategory = () =>{

  setSubcategoriesCard(
  <Grid xs={12} md={3} sx={{
    display : 'flex',
    flexDirection : 'column',
    textAlign :'center',
    alignItems : 'center',
  }}
  
  >
        <SubcategoryCard onSubmit={onSubmit} categoryId={categoryId} />
    </Grid>
  )
}

const onSubmit = (data) =>{
  console.log(data)
  if(data.icon == null){
    setMessage("Please select a valid image.")
    setFlag("error")
    setOpen(true)
    return false
  }
  axios.defaults.headers = {
      Authorization : auth.token,
  }
  axios.post(host+"/admin/item/subcategory/add",data)
  .then(res => {
    setMessage(res.data.message)
    setFlag("success")
    setOpen(true)
    reset()
  }).catch(err=>{
      setMessage(!!err.response  ? err.response.data.message : err.message)
      setFlag("error")
      setOpen(true)
  })

}


const reset = () =>{
  setValues({})
}


return ( <>
<Box
    component="main"
    sx={{
        flexGrow: 1,
        py: 8,
    }}
      >
  <Container maxWidth="xl">
      <Stack spacing={3}>
      <div style={{display : 'flex',justifyContent:'flex-end'}} >
          <Button
            startIcon={(
              <SvgIcon fontSize="small">
                <PlusIcon />
              </SvgIcon>
            )}
            variant="contained"
            onClick={addSubCategory}
          >
            Add Subcategory
          </Button>
      </div>

 

<Grid xs={12} md={6} lg={8}>
  <Card>
      <CardHeader title="Add category"/>
      <CardContent>
        <Box sx={{ m: -1.5 }}>
        <Grid container spacing={3}>
            <Grid xs={12} md={2}>
                <Box>
                  <ImageInput totalImage={0} avtar = {category.icon}/>
                </Box>
            </Grid>

            <Grid xs={12} md={6}>
              <TextField
                sx={{mt:3}}
                fullWidth
                label="Category Name"
                name="category"
                onChange={handleChange}
                required={true}
                value={category.category}
                InputLabelProps={{shrink :true}}
              />
            </Grid>
          </Grid>
        </Box>
      </CardContent>
      <Divider/>
      <CardActions sx={{ justifyContent: 'flex-end' }}>
      <Link href={"/item/category/update/"+categoryId}>
        <Button type="submit" variant="contained">
          Edit
        </Button>
      </Link>
      </CardActions>
    </Card>
  </Grid>

    <Grid xs={12} md={12} container spacing={3} >
    {subcategoryCard}
    
    {subcategories.map((subcategory , i)=>{
      return <Grid xs={12} md={3} sx={{
        display : 'flex',
        flexDirection : 'column',
        textAlign :'center',
        alignItems : 'center'
      }}>
         <SubcategoryCard onSubmit={onSubmit} categoryId={categoryId} subcategory = {subcategory} buttonLabel={"Update"}/>
    </Grid>
  })} 

</Grid>

</Stack>
</Container>
</Box>

  <Snackbar anchorOrigin={{ vertical : 'top', horizontal : 'right' }}
    open={open}
    onClose={handleClose}
    key={'top' + 'right'}
  >
    <Alert onClose={handleClose} severity={flag} sx={{ width: '100%' }}>
        {message}
    </Alert>
  </Snackbar>

</>
)
}


createItem.getLayout = (page) => (
<DashboardLayout>
{page}
</DashboardLayout>
);

export default createItem;