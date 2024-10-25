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
  Alert,
  Stack,
  Container,
  SvgIcon
} from "@mui/material";
import { Image, Upload } from "antd";
import axios from "axios";
import { useRouter } from "next/router";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "src/hooks/use-auth";
import { ArrowButtons } from "src/layouts/arrow-button";
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import ImageInput from "src/sections/image-input";
import { host, storeImage } from "src/utils/util";
import RefreshIcon from '@mui/icons-material/Refresh';




const  UpdateWholesale = () =>{
const [open,setOpen] = useState(false)
const [message,setMessage] = useState("")
const [flag,setFlag] = useState("warning")
const auth = useAuth()
const[cityList,setCityList] = useState([])
const[stateList,setStateList] = useState([])
const [selectedState , setSelectedState] = useState(1)
const [store,setStore] = useState({})
const [address,setAddress] = useState(store.address)

const router = useRouter()
const {slug} = router.query
const [categories,setItemCategories] = useState([])
const [subcategories,setItemSubCategories] = useState([])


useEffect(()=>{
  axios.defaults.headers={
      Authorization : auth.token,
  }

  axios.get(host+"/admin/store/detail/"+slug)
  .then(res=>{
      let response = res.data.res;
      setStore({
        ...response,
        category : response.storeCategory.id,
        subcategory : response.storeSubCategory.id,
        storeCategory : undefined,
        storeSubCategory : undefined
      })
      console.log(response)
      setAddress(response.address)
      setSelectedState(response.address.state)
})
  .catch(err=>console.log(err))
},[slug])


useEffect(()=>{
axios.defaults.headers={
    Authorization : auth.token
}
axios.get(host+"/admin/address/state")
.then(res=>setStateList(res.data))
.catch(err=>console.log(err))
},[])



useEffect(()=>{
axios.defaults.headers={
  Authorization : auth.token
}
axios.get(host+`/admin/address/city/${selectedState}`)
.then(res=>{
    setCityList(res.data)}
    )
.catch(err=>console.log(err))
},[selectedState])




const changeState = useCallback(
    async (event) => {
      let stateId =  event.target.value
      setSelectedState(stateId)
      setAddress({...address, state : stateId})
  },
  []
);


const changeCity = (event) => {
    let cityId =  event.target.value
    setAddress({...address, city : cityId})
}



useEffect(() => {
  const getData = async () => {
      axios.defaults.headers = {
          Authorization: auth.token
      }
      await axios.get(host + "/admin/store/category")
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
    await axios.get(host + "/admin/store/subcategory/"+store.category)
        .then(res => {
            const data = res.data;
            setItemSubCategories(data)
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
}, [store.category]) 



const handleChange = useCallback ((event) => {
    setStore((preState) =>({
      ...preState,
      [event.target.name]: event.target.value
    }));
  },[])


const handleSubmit = useCallback(async (e) =>{
  e.preventDefault()
  const form = e.target;
  const formData = new FormData(form)
  console.log(store)
  let storeData = {
    ...store,
    storeSlug : store.slug,
    addressSlug : store.address.slug,
    description : formData.get("description"),
    storeEmail : formData.get("email"),
    storePhone : formData.get("phone"),
    state:  formData.get("state"),
    city :  formData.get("city"),
    street:  formData.get("street"),
    zipCode :  formData.get("zipCode"),
    categoryId: formData.get("category"),
    subCategoryId: formData.get("subcategory"),
    storeName :  formData.get("storeName")
  }
  
    axios.defaults.headers = {
        Authorization : auth.token,
         "Content-Type" : "multipart/form-data"
    }
    await axios.post(host+"/admin/store/update",storeData)
    .then(res => {
      setStore((previous) => ({...previous , avtar : !!store.storePic ? store.storePic.name  : store.avtar }))  
      setMessage(res.data.message)
      setFlag("success")
      form.reset();
      setStore({})
      setAddress({})
    }).catch(err=>{
        let errResponse = err.response
        setMessage(!!errResponse ? errResponse.data.message : err.message)
        setFlag("error")
    })
    setOpen(true)
  })

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
              onChange={handleChange}
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
              onChange={handleChange}
              required
              InputProps={{ maxLength: 6 }}
              value={!!address ? address.zipCode : ''}
              InputLabelProps={{shrink : true}}
            />

          </Grid>


          <Grid
            xs={12}
            md={6}
          >      
          <FormControl fullWidth>
          <InputLabel id="stateLabel">State</InputLabel>
            <Select
              labelId="stateLable"
              id="demo-simple-select"
              name='state'
              value={!!address ? address.state: 0}
              label="State"
              onChange={changeState}
              required
              InputLabelProps={{ shrink: true }}
            >
            {stateList.map((state,i)=>{
                return (<MenuItem key={i+state.stateName} value={state.id}>{state.stateName}</MenuItem>)
            })}
    
            </Select>
        </FormControl>
          </Grid>


          <Grid
            xs={12}
            md={6}
          >
          <FormControl fullWidth>
            <InputLabel id="cityLabel">City</InputLabel>
            <Select
              fullWidth
              labelId="cityLabel"
              name='city'
              label="City"
              value={!!address ? address.city : 0}
              onChange={changeCity}
              InputLabelProps={{ shrink: true }}
            >
            {cityList.map((city,i) => {
                return (<MenuItem key={i} value={city.id}>{city.cityName}</MenuItem>)
            })}
            </Select> 
            </FormControl>
          </Grid>




                {/* Category */}
                <Grid
                      xs={12}
                      md={6}
                  >
                      <FormControl fullWidth>
                          <InputLabel id="itemLabel">Category</InputLabel>
                          <Select
                              labelId="itemLabel"
                              id="category"
                              name='category'
                              value={store.category !=undefined ? ""+store.category : ""}
                              label="Category"
                              onChange={handleChange}
                              required
                          >
                          {categories.map((categroyObj , i) => {
                              return ( <MenuItem key={i} value={categroyObj.id}>{categroyObj.category}</MenuItem>
                              )})
                          }
                          </Select>
                      </FormControl>
                  </Grid>

                    {/* Subcategory */}
                    <Grid
                      xs={12}
                      md={6}
                  >
                      <FormControl fullWidth>
                          <InputLabel id="itemLabel">Subcategory</InputLabel>
                          <Select
                              labelId="itemLabel"
                              id="subcategory"
                              name='subcategory'
                              value={store.subcategory !=undefined ? ""+store.subcategory : ""}
                              label="Subcategory"
                              onChange={handleChange}
                              required
                          >
                          {subcategories.map((subcategroyObj , i) => {
                              return ( <MenuItem key={i} value={subcategroyObj.id}>{subcategroyObj.subcategory}</MenuItem>
                              )})
                          }
                          </Select>
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