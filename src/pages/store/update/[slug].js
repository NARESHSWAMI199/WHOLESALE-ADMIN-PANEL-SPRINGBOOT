import RefreshIcon from '@mui/icons-material/Refresh';
import {
  Alert,
  Autocomplete,
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Container,
  Divider,
  FormControl,
  Unstable_Grid2 as Grid,
  InputLabel,
  MenuItem,
  Select,
  Snackbar,
  Stack,
  SvgIcon,
  TextField
} from "@mui/material";
import axios from "axios";
import { useRouter } from "next/router";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "src/hooks/use-auth";
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import ImageInput from "src/sections/image-input";
import { host, storeImage } from "src/utils/util";




const  UpdateWholesale = () =>{
const [open,setOpen] = useState(false)
const [message,setMessage] = useState("")
const [flag,setFlag] = useState("warning")
const auth = useAuth()
const[cityList,setCityList] = useState([])
const[stateList,setStateList] = useState([])
const [store,setStore] = useState({})
const [address,setAddress] = useState()
const router = useRouter()
const {slug} = router.query
const [categories,setItemCategories] = useState([])
const [subcategories,setItemSubCategories] = useState([])
const [values,setValues] = useState({})

useEffect(()=>{
  axios.defaults.headers={
      Authorization : auth.token,
  }

  // Setting the store detail
  axios.get(host+"/admin/store/detail/"+slug)
  .then(res=>{
      let resStore = res.data.res;

      setStore({
        ...resStore,
      })
      console.log(resStore)
      setAddress(resStore.address)
      setValues(
        {
          street :resStore?.address?.street,
          zipCode : resStore?.address?.zipCode,
          storeEmail : resStore?.email,
          storePhone : resStore?.phone,
          category : {label : resStore?.storeCategory?.category, id : resStore?.storeCategory?.id},
          subcategory : {label : resStore?.storeSubCategory?.subcategory, id : resStore?.storeSubCategory?.id},
        }
      )
})
  .catch(err=>console.log(err))
},[slug])


useEffect(()=>{
    // do't call api if store is null or undefiend
    if(!store) return 
    axios.defaults.headers={
        Authorization : auth.token
    }
    axios.get(host+"/admin/address/state")
    .then(res=>{
      setStateList(res.data)
      let selectedState  = res.data.find(state=>state.id === address?.state);
      setValues((prevState)=>({
        ...prevState,
        state : {
          label : selectedState?.stateName  || '', id : selectedState?.id
        } 
      }))
    })
    .catch(err=>console.log(err))
  },[store])



useEffect(()=>{
  const getCity = async () => { 
      axios.defaults.headers={
        Authorization : auth.token
      }
      axios.get(host+`/admin/address/city/${values.state?.id}`)
      .then(res=>{
          setCityList(res.data)
          let selectedCity = res.data.find(city=>city.id === address?.city)
          setValues((prevState)=>({...prevState, city : {label : selectedCity?.cityName || '', id : selectedCity?.id}}))
        })
      .catch(err=>console.log(err))
  }
  if(values.state != undefined && values.state.id != undefined){
    getCity()
  }
},[values.state])



useEffect(() => {
  const getData = async () => {
      axios.defaults.headers = {
          Authorization: auth.token
      }
      await axios.post(host + "/admin/store/category",{order :'asc' , orderBy : 'category'})
          .then(res => {
              const data = res.data;
              setItemCategories(data)
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
const getSubcategory = async () => {
    axios.defaults.headers = {
        Authorization: auth.token
    }
    await axios.post(host + "/admin/store/subcategory", {categoryId : values.category?.id,order :'asc' , orderBy : 'subcategory'})
        .then(res => {
            const data = res.data;
            setItemSubCategories(data)
            setValues((prevState)=>({...prevState, subcategory : {label : store?.storeSubCategory?.subcategory || '', id : store?.storeSubCategory?.id}}))
        })
        .catch(err => {
            setMessage(!!err.response ? err.response.data.message : err.message)
            setFlag('error')
            setOpen(true)
        })
}
if(store.category !=undefined){
    getSubcategory();
}
}, [values.category]) 



const handleChangeAddress = useCallback ((event) => {
  setAddress((preState) =>({
    ...preState,
    [event.target.name]: event.target.value
  }));
},[])


const handleChange = useCallback ((event) => {
    setStore((preState) =>({
      ...preState,
      [event.target.name]: event.target.value
    }));
  },[])


const handleSubmit = async (e) =>{
  e.preventDefault()
  const form = e.target;
  const formData = new FormData(form)
  console.log(store)

  // don't  want send in request
  delete store.storeCategory
  delete store.storeSubCategory
  delete store.address

  let storeData = {
    ...store,
    storeSlug : store.slug,
    storeName :  formData.get("storeName"),
    description : formData.get("description"),
    storeEmail : formData.get("email"),
    storePhone : formData.get("phone"),
    street:  formData.get("street"),
    zipCode :  formData.get("zipCode"),
    state:  values.state?.id,
    city :  values.city?.id,
    categoryId: values.category?.id,
    subCategoryId: values.subcategory?.id
  }
  
    axios.defaults.headers = {
        Authorization : auth.token,
         "Content-Type" : "multipart/form-data"
    }
    await axios.post(host+"/admin/store/update",storeData)
    .then(res => {
      // setStore((previous) => ({...previous , avtar : !!store.storePic ? store.storePic.name  : store.avtar }))  
      setMessage(res.data.message)
      setFlag("success")
      form.reset();
      setStore({})
      setValues({})
      setAddress({})
      setOpen(true)
    }).catch(err=>{
        let errResponse = err.response
        setMessage(!!errResponse ? errResponse.data.message : err.message)
        setFlag("error")
        setOpen(true)
    })

  }

const handleClose = useCallback(()=>{
      setOpen(false)
})
 



const onSubmit = (image) =>{
setStore((pervious)=>({
  ...pervious,
  storePic : image.originFileObj,
}))
}


return ( <>

<Box
  component="main"
  sx={{
    flexGrow: 1,
    py: 8
  }}
>
  <Container maxWidth="xl">
    <Stack spacing={3}>


<Grid
  xs={12}
  md={6}
  lg={8}
>

  <form
  autoComplete="off"
  onSubmit={handleSubmit}
>
  <Card>
    <CardHeader
      //subheader="From here you can add store."
      title="Store Detail"
    />
    <CardContent sx={{ pt: 0 }}>
      <Box sx={{ m: -1.5 }}>
        <Grid
          container
          spacing={3}
        >

        {/* store image input */}

        <div style={{marginLeft : '10px',marginTop: '10px'}}>
        <ImageInput onChange={onSubmit} avtar={storeImage+store.slug+"/"+store.avtar}/>
        </div>
          <Grid
            xs={12}
            md={12}
          >
            <TextField
              fullWidth
              label="Store Name"
              name="storeName"
              onChange={handleChange}
              required
              value={store.storeName}
              InputLabelProps={{ shrink: true }}
            />

          </Grid>


          {/* address */}
          <Grid
            xs={12}
            md={6}
          >
            <TextField
              fullWidth
              label="Street Address"
              name="street"
              onChange={handleChangeAddress}
              required
              value={!!address ? address.street :''}
              InputLabelProps={{shrink : true}}
            />

          </Grid>


          <Grid
            xs={12}
            md={6}
          >
            <TextField
              fullWidth
              label="Zip Code"
              name="zipCode"
              type="number"
              onChange={handleChangeAddress}
              required
              InputProps={{ maxLength: 6 }}
              value={!!address ? address.zipCode : ''}
              InputLabelProps={{shrink : true}}
            />

          </Grid>


          <Grid
            xs={12}
            md={6}
            item
          >      
           {/* address */}
          <FormControl fullWidth>
            <Autocomplete
                disablePortal
                options={[...stateList.map((state)=>({label : state.stateName, id : state.id}))]}
                fullWidth
                name="state"
                value={values.state?.label || ''}
                onChange={(e,value)=>setValues((prevState)=>({...prevState, state : value }))}
                renderInput={(params) => <TextField required {...params} label="State" />} >
            </Autocomplete> 

        </FormControl>
          </Grid>


          <Grid
            xs={12}
            md={6}
          >
          <FormControl fullWidth>
              <Autocomplete
                  disablePortal
                  options={[...cityList.map((city)=>({label : city.cityName, id : city.id}))]}
                  fullWidth
                  value={values.city?.label || ''}
                  onChange={(e,value)=>setValues((prevState)=>({  ...prevState, city : value}))}
                  renderInput={(params) => <TextField name="city" required {...params} label="City" />} >
              </Autocomplete> 
            </FormControl>
          </Grid>




                {/* Category */}
                <Grid
                      xs={12}
                      md={6}
                  >
                      <FormControl fullWidth>
                          <Autocomplete
                                disablePortal
                                options={[...categories.filter(category=> category.id !== 0).map((category)=>({label : category.category, id : category.id})),{label : 'Other', id : 0}]} 
                                fullWidth
                                name={"category"}
                                value={values.category?.label || ''}
                                onChange={(e,value)=>setValues((prevState)=>({...prevState, category : value    }))}
                                renderInput={(params) => <TextField required {...params} label="Categeory" />} >
                            </Autocomplete> 
                      </FormControl>
                  </Grid>

                    {/* Subcategory */}
                    <Grid
                      xs={12}
                      md={6}
                  >
                      <FormControl fullWidth>
                            <Autocomplete
                                disablePortal
                                required
                                options={[...subcategories.filter(subcategory => subcategory.id !== 0).map((subcategory)=>({label : subcategory?.subcategory, id : subcategory?.id})),{label : 'Other', id : 0}]}
                                fullWidth
                                name="subcategory"
                                value={values.subcategory?.label || ''}
                                onChange={(e,value)=>setValues((prevState)=>({  ...prevState, subcategory : value}))}
                                renderInput={(params) => <TextField  required {...params} label="Subcategory" />} >
                            </Autocomplete> 
                      </FormControl>
                  </Grid>



          <Grid
            xs={12}
            md={6}
          >
            <TextField
              fullWidth
              label="Store Email Address"
              name="email"
              onChange={handleChange}
              required
              type="email"
              value={store.email}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid
            xs={12}
            md={6}
          >
            <TextField
              fullWidth
              label="Store Phone Number"
              name="phone"
              onChange={handleChange}
              type="number"
              value={store.phone}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
           <Grid
            xs={12}
            md={12}
          >
            <TextField
              fullWidth
              label="Description"
              name="description"
              onChange={handleChange}
              required
              multiline
              rows={4}
              value={!!store.description ? store.description : ""}
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
        </Grid>
      </Box>
    </CardContent>
    <Divider />


  <CardActions sx={{ justifyContent: 'flex-end' }}>
      <Button type="submit" variant="contained">
        Save details
      </Button>
      <Button
          startIcon = {
              <SvgIcon fontSize="small">
              <RefreshIcon />
              </SvgIcon>
          }
          sx={{color:'text-secondary'}}
          onClick={(e) => window.location.reload()}
            variant="contained"
      >
      </Button>
    </CardActions>
  </Card>
</form>
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


UpdateWholesale.getLayout = (page) => (
<DashboardLayout>
{page}
</DashboardLayout>
);

export default UpdateWholesale;