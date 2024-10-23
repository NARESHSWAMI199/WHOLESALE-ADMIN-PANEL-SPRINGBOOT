import { Box,
    Button, 
    Card,
    CardActions,
    CardContent,
    CardHeader,
    MenuItem,
    Select,
    TextField,
    Unstable_Grid2 as Grid,
    InputLabel,
    FormControl,
    Snackbar,
    Alert,
    Container,
    Stack
} from "@mui/material";
import axios from "axios";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "src/hooks/use-auth";
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { host } from "src/utils/util";
import ImageInput from "src/sections/image-input";
import { Divider } from "antd";
import { useRouter } from "next/router";

var categoryIcon = null

const CreateSubcategory = () =>{    
const router = useRouter()
const {categoryId} = router.query 
const [open,setOpen] = useState(false)
const [message,setMessage] = useState("")
const [flag,setFlag] = useState("success")

const auth = useAuth()
const [values,setValues] = useState({categoryId : categoryId})
const [categories, setCategories] = useState([])


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

useEffect(() => {
    const getData = async () => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        await axios.get(host + "/admin/item/category")
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
    getData();

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
  


  const handleSubmit = useCallback(
    (e) =>{
    e.preventDefault()
    const form = e.target;
    const formData = new FormData(form)
    if(categoryIcon == null){
      setMessage("Please select a valid image.")
      setFlag("error")
      setOpen(true)
      return false
    }
    let data = {
        categoryId :  formData.get("category"),
        subcategory : formData.get("subcategory"),
        icon : categoryIcon
      }

    axios.defaults.headers = {
        Authorization : auth.token,
    }
    axios.post(host+"/admin/item/subcategory/add",data)
    .then(res => {
      setMessage(res.data.message)
      setFlag("success")
      setOpen(true)
      form.reset();
      reset()
    }).catch(err=>{
        setMessage(!!err.response  ? err.response.data.message : err.message)
        setFlag("error")
        setOpen(true)
    })

  },[])

   

const reset = () =>{
  setValues({})
}


const onSubmit = (image) =>{
  if(!!image)
  generateThumbnail(image.originFileObj)
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
        title="Create subcategory"
      />
      <CardContent sx={{ pt: 2 }}>
        <Box sx={{ m: -1.5 }}>

        <Grid
            container
            spacing={3}
          >
            <Grid
                xs={12}
                md={12}
                >
            <Box>
                <ImageInput onChange={onSubmit} avtar = {values.icon}/>
            </Box>
            </Grid>
                
            <Grid
                xs={12}
                md={6}
            >
                <FormControl fullWidth>
                <InputLabel id="category">Category</InputLabel>
                <Select
                    labelId="category"
                    id="demo-simple-select"
                    name='category'
                    onChange={handleChange}
                    value={values.categoryId}
                >
                    {categories.map((category,i)=>{
                        return <MenuItem 
                        key={i}
                        value={category.id}>{category.category}</MenuItem>
                    })}

                </Select>
                </FormControl>
            </Grid>

                <Grid
                xs={12}
                md={6}
                >
                <TextField
                    fullWidth
                    label="Subcategory Name"
                    name="subcategory"
                    onChange={handleChange}
                    required={true}
                    value={values.subcategory}
                    // InputLabelProps={{shrink :true}}
                />
                </Grid>
            </Grid>
        </Box>
      </CardContent>
      <Divider/>
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


CreateSubcategory.getLayout = (page) => (
<DashboardLayout>
  {page}
</DashboardLayout>
);

export default CreateSubcategory;