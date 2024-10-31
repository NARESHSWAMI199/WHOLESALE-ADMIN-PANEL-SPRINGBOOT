import { Box,
  Button, 
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Divider,
  MenuItem,
  Select,
  Unstable_Grid2 as Grid,
  InputLabel,
  FormControl,
  Snackbar,
  Alert,
  Container,
  Stack,
  SvgIcon,
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
import { number } from "prop-types";



const StoreCategories = () =>{    

const router = useRouter()
const {categoryId} = router.query
/** WE CAN'T USE STATE FOR BASE 64 LARGE DATA STATES WILL NOT WORK FOR LARGE DATA*/


const [open,setOpen] = useState(false)
const [message,setMessage] = useState("")
const [flag,setFlag] = useState("success")

const auth = useAuth()
// const [values,setValues] = useState({})
const [subcategories,setSubcategories] = useState([])
const [subcategoryCard,setSubcategoriesCard] = useState();
const [values,setValues] = useState({category : categoryId})
const [categories,setCategories] = useState([])
const [newSubCategroyAdded, setNewSubCategoryAdded] = useState(false)
const [newCard,setNewCard] = useState(false)

useEffect(() => {
  const getAllCategories = async () => {
      axios.defaults.headers = {
          Authorization: auth.token
      }
      await axios.post(host + "/admin/store/category",{order :'asc' , orderBy : 'category'})
          .then(res => {
              const data = res.data;
              setCategories(data)
          })
          .catch(err => {
              setMessage(!!err.response ? err.response.data.message : err.message)
              setFlag('error')
              setOpen(true)
          })
  }
  getAllCategories();

}, [])

useEffect(() => {
  const getCategory = async () => {
      axios.defaults.headers = {
          Authorization: auth.token
      }
      await axios.get(host + "/admin/store/category/"+values.category)
          .then(res => {
              const data = res.data;
              setValues({...data, category : data.id})
          })
          .catch(err => {
              setMessage(!!err.response ? err.response.data.message : err.message)
              setFlag('error')
              setOpen(true)
          })
  }
  if(values.category !=undefined){
    getCategory();
  }
}, [values.category])



useEffect(() => {
  const getAllSubcategories = async () => {
      axios.defaults.headers = {
          Authorization: auth.token
      }
      await axios.post(host + "/admin/store/subcategory/",{categoryId : values.category,orderBy : 'updatedAt'})
          .then(res => {
              const data = res.data;
              setSubcategories(data)
          })
          .catch(err => {
              setMessage(!!err.response ? err.response.data.message : err.message)
              setFlag('error')
              setOpen(true)
          })
  }
  if(values.category != undefined){
    getAllSubcategories();
  }
console.log(values.category)
}, [values.category,newSubCategroyAdded])



const onDelete = (subCategorySlug) => {
  if(subCategorySlug == undefined || subCategorySlug == null){
    setSubcategoriesCard("")
  }else{
    axios.defaults.headers = {
      Authorization :  auth.token  
    }
    axios.get(host+`/admin/store/subcategory/delete/${subCategorySlug}`)
    .then(res => {
        setFlag("success")
        setMessage(res.data.message)
        setOpen(true)
        setSubcategories((prevSubcategories) => prevSubcategories.filter((s) => s.slug !== subCategorySlug));
    }).catch(err => {
      console.log(err)
      setFlag("error")
      setMessage(!!err.response ? err.response.data.message : err.message)
      setOpen(true)
    } )
  }
}



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
    alignItems : 'center'
  }}
  
  >
        <SubcategoryCard 
          onDelete={onDelete} 
          onSubmit={onSubmit} 
          categoryId={values.category}
        />
    </Grid>
  )
}

const onSubmit = (data) =>{
  // console.log(data)
  if(data.icon == null){
    setMessage("Please select a valid image.")
    setFlag("error")
    setOpen(true)
    return false
  }
  axios.defaults.headers = {
      Authorization : auth.token,
  }
  axios.post(host+"/admin/store/subcategory/add",data)
  .then(res => {
    setMessage(res.data.message)
    setFlag("success")
    setOpen(true)
    setNewSubCategoryAdded((pre) => pre ? false : true)
    setSubcategoriesCard("")
    if(data.id == undefined || data.id == null){
      setTimeout(()=>{
        addSubCategory(',')
      },100)
     
    } 
  }).catch(err=>{
      setMessage(!!err.response  ? err.response.data.message : err.message)
      setFlag("error")
      setOpen(true)
  })

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

      <div>
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


      <div  style={{marginLeft : 4}}>
        <Link
              href={{
                  pathname: '/store/category/create',
                }}
              >
          <Button
            startIcon={(
              <SvgIcon fontSize="small">
                <PlusIcon />
              </SvgIcon>
            )}
            variant="contained"
          >
            Add Category
          </Button>
          </Link>
      </div>
    </div>
 

<Grid xs={12} md={8} lg={8}>
  <Card>
      <CardHeader title="Add Store Category"/>
      <CardContent>
        <Box sx={{ m: -1.5 }}>
        <Grid container spacing={3}>
            <Grid xs={12} md={2}>
                <Box sx={{
                    textAlign : 'center'
                   }}>
                  <ImageInput totalImage={0} avtar = {values.icon}/>
                </Box>
            </Grid>

              {/* Category */}
              <Grid
                xs={12}
                md={8}
                sx={{
                  mt : 3
                }}
            >
                <FormControl fullWidth>
                    <InputLabel id="itemLabel">Category</InputLabel>
                    <Select
                        labelId="itemLabel"
                        id="category"
                        name='category'
                        value={""+values.category}
                        label="Category"
                        onChange={handleChange}
                    >
                    {categories.map((categroyObj , i) => {
                        return ( <MenuItem key={i} value={categroyObj.id}>{categroyObj.category}</MenuItem>
                        )})
                    }
                    </Select>
                </FormControl>
            </Grid>
          </Grid>
        </Box>
      </CardContent>
      <Divider/>
      <CardActions sx={{ justifyContent: 'flex-end' }}>
      <Link href={"/store/category/update/"+values.category}>
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
      return <Grid
        key={i}
        xs={12} md={3} sx={{
        display : 'flex',
        flexDirection : 'column',
        textAlign :'center',
        alignItems : 'center'
      }}>
         <SubcategoryCard onSubmit={onSubmit} onDelete={onDelete} categoryId={values.category} subcategory = {subcategory} buttonLabel={"Update"}/>
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


StoreCategories.getLayout = (page) => (
<DashboardLayout>
{page}
</DashboardLayout>
);

export default StoreCategories;