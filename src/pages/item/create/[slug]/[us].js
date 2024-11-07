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
    Stack
} from "@mui/material";
import axios from "axios";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "src/hooks/use-auth";
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { host } from "src/utils/util";
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import { useRouter } from "next/router";
import { redirect } from "next/navigation";
import ImageInput from "src/sections/image-input";
import { ArrowButtons } from "src/layouts/arrow-button";


const CreateItem = () =>{    

const router = useRouter()
const {slug,us} = router.query

const [open,setOpen] = useState(false)
const [message,setMessage] = useState("")
const [flag,setFlag] = useState("success")

const auth = useAuth()
const [values,setValues] = useState({})
const [categories,setItemCategories] = useState([])
const [subcategories,setItemSubCategories] = useState([])


const handleChange = useCallback(
  (event) => {
      if ([event.target.name] == 'subcategory'){
          for(let subcategory of subcategories){
              console.log(subcategory.id + " "+event.target.value )
              if(subcategory.id ==  event.target.value){
                  setValues((prevState) => ({
                      ...prevState,
                      unit : subcategory.unit,
                      [event.target.name]: event.target.value
                  }));
                  break
              }
          }
      }else{
          setValues((prevState) => ({
              ...prevState,
              [event.target.name]: event.target.value
          }));
      }
  },
  [subcategories]
);



  
  useEffect(() => {
    const getData = async () => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        await axios.post(host + "/admin/item/category",{orderBy:'category',order :'asc'})
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
      await axios.post(host + "/admin/item/subcategory",{categoryId : values.category,orderBy:'subcategory',order :'asc'})
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
  if(values.category !=undefined){
      getSubcategory();
  }
}, [values.category]) 



  const handleSubmit = useCallback(
    (e) =>{
    e.preventDefault()
    const form = e.target;
    const formData = new FormData(form)
    let item = {
        name : formData.get("name"),
        price: formData.get("price"),
        discount: formData.get("discount"),
        inStock: formData.get("inStock") ? 'Y' : 'N',
        label: formData.get("itemLabel"),
        description: formData.get("description"),
        categoryId: formData.get("category"),
        subCategoryId: formData.get("subcategory"),
        capacity : !!values.unit && values.unit != 'null' ? formData.get('capacity') : 0 ,
        wholesaleSlug : slug,
        itemImage : values.itemImage
      }

    axios.defaults.headers = {
        Authorization : auth.token,
        "Content-Type" : "multipart/form-data"
    }
    axios.post(host+"/admin/item/add",item)
    .then(res => {
      setMessage(res.data.message)
      setFlag("success")
      form.reset();
      reset()
      setOpen(true)
    }).catch(err=>{
        setMessage(!!err.response  ? err.response.data.message : err.message)
        setFlag("error")
        setOpen(true)
    })

  })

  const handleClose = useCallback(()=>{
        setOpen(false)
  })
   

const reset = () =>{
  setValues({})
}


const onSubmit = (image) =>{
  console.log(image)
  setValues((pervious)=>({
    ...pervious,
    itemImage : image.originFileObj
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
        subheader="From here you can add a new item."
        title="Create Item"
      />
      <CardContent sx={{ pt: 0 }}>
        <Box sx={{ m: -1.5 }}>

        <div style={{marginLeft : '10px',marginTop: '10px'}}>
          <ImageInput onChange={onSubmit}/>
        </div>

          <Grid
            container
            spacing={3}
          >

          {/* ITEM NAME */}

            <Grid
              xs={12}
              md={12}
            >
              <TextField
                fullWidth
                label="Item Name"
                name="name"
                onChange={handleChange}
                required={true}
                value={values.itemName}
              />

            </Grid>

      {/* PRICE */}

            <Grid
              xs={12}
              md={6}
            >
              <TextField
                fullWidth
                label="Price"
                name="price"
                onChange={handleChange}
                required={true}
                type="number"
                value={values.itemPrice}
                InputLabelProps={{shrink: true}}
              />

            </Grid>


                  {/* DISCOUNT */}

            <Grid
              xs={12}
              md={6}
            >
              <TextField
                fullWidth
                label="Discount"
                name="discount"
                onChange={handleChange}
                required={true}
                type="number"
                value={values.itemDiscount}
              />

            </Grid>



         

          
              {/* Category */}
              <Grid
                xs={12}
                md={6}
            >
                <FormControl fullWidth>
                    <InputLabel style={{background :'white'}} id="itemLabel">Category</InputLabel>
                    <Select
                        labelId="itemLabel"
                        id="category"
                        name='category'
                        value={""+values.category}
                        onChange={handleChange}
                    >
                    {categories.map((categroyObj , i) => {
                        if(categroyObj.id !=0)
                        return ( <MenuItem key={i} value={categroyObj.id}>{categroyObj.category}</MenuItem>
                        )})
                    }
                    <MenuItem value={0}>{"Other"}</MenuItem>
                    </Select>
                </FormControl>
            </Grid>


    {/* Subcategory */}
    <Grid
      xs={12}
      md={6}
  >
      <FormControl fullWidth>
          <InputLabel style={{background :'white'}} id="itemLabel">Subcategory</InputLabel>
          <Select
              labelId="itemLabel"
              id="subcategory"
              name='subcategory'
              value={""+values.subcategory}
              onChange={handleChange}
          >
          {subcategories.map((subcategroyObj , i) => {
                if(subcategroyObj.id !=0)
              return ( <MenuItem key={i} value={subcategroyObj.id}>{subcategroyObj.subcategory}</MenuItem>
              )})
          }
              <MenuItem value={0}>{"Other"}</MenuItem>
          </Select>
      </FormControl>
  </Grid>



                                    
    {/* capacity */}
    {!!values.unit && values.unit != 'null' ?
    <Grid
        xs={12}
        md={6}
    >
        <TextField
            fullWidth
            label={"Capacity/Weight in "+values.unit}
            name="capacity"
            onChange={handleChange}
            required={true}
            type="number"
            value={values.capacity}
            InputLabelProps={{ shrink: true }}
        />

    </Grid> : ''
  }


      <Grid
            xs={12}
            md={6}
          >
            <FormControl fullWidth>
          <InputLabel style={{background :'white'}} id="itemLabel">Label</InputLabel>
          <Select
            labelId="itemLabel"
            id="demo-simple-select"
            name='itemLabel'
            value={values.itemLabel}
            onChange={handleChange}
          >
            <MenuItem value={"O"}>Old</MenuItem>
            <MenuItem value={"N"}>New</MenuItem>
        
          </Select>
          </FormControl>
          </Grid>

          
          <Grid
              xs={12}
              md={6}
            >
          <TextField
            fullWidth
            label="Description"
            name="description"
            onChange={handleChange}
            required={true}
            multiline
            value={values.itemDiscription}
            rows={2}
          />
        </Grid>

        <Grid
              xs={12}
              md={6}
            >
              <FormGroup>
        <FormControlLabel control={<Checkbox  
                    name="inStock"
                    onChange={handleChange}
                    value={values.itemInStock} />}
                  label="In stock" />
      </FormGroup>
        </Grid>
          </Grid>
        </Box>
      </CardContent>
      <Divider />
    <CardActions sx={{ justifyContent: 'flex-end' }}>
        <Button type="submit" variant="contained">
          Save details
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


CreateItem.getLayout = (page) => (
<DashboardLayout>
  {page}
</DashboardLayout>
);

export default CreateItem;