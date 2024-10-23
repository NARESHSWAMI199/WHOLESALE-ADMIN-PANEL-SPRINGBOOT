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
import { tr } from "date-fns/locale";


const UpdateStoreCategory = () =>{    

const router = useRouter()
const {categoryId} = router.query
/** WE CAN'T USE STATE FOR BASE 64 LARGE DATA STATES WILL NOT WORK FOR LARGE DATA*/


const [open,setOpen] = useState(false)
const [message,setMessage] = useState("")
const [flag,setFlag] = useState("success")

const auth = useAuth()
const [values,setValues] = useState({})




useEffect(() => {
    const getData = async () => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        await axios.get(host + "/admin/store/category/"+categoryId)
            .then(res => {
                const data = res.data;
                categoryIcon = data.icon
                setValues(data)
            })
            .catch(err => {
                setMessage(!!err.response ? err.response.data.message : err.message)
                setFlag('error')
                setOpen(true)
            })
    }
    getData();

}, [])

let categoryIcon = values.icon

  const handleChange = useCallback(
    (event) => {
      setValues((prevState) => ({
        ...prevState,
        [event.target.name]: event.target.value
      }));
    },
    []
  );


  const generateThumbnail = (file) => {
    console.log("hello")
    console.log(file)
      if(!file) return false
      if(file.size/1024  > 300) {
        setFlag("error")
        setMessage("File is large according to icon image.")
        setOpen(true)
      }
      let reader = new FileReader();
      reader.onload = (event) => {
          const base64Data = event.target.result;
          console.log(base64Data)
          categoryIcon = base64Data
      }
      reader.readAsDataURL(file);
   
}

  


  const handleSubmit = useCallback(
    (e) =>{
    e.preventDefault()
    const form = e.target;
    const formData = new FormData(form)
    console.log(categoryIcon)
    let data = {
        id : categoryId,
        category : formData.get("category"),
        icon : categoryIcon
      }

    axios.defaults.headers = {
        Authorization : auth.token,
    }
    axios.post(host+"/admin/item/category/add",data)
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

  const handleClose = useCallback(()=>{
        setOpen(false)
  })
   

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
        // subheader="From here you can add a new item."
        title="Create category"
      />
      <CardContent sx={{ pt: 4 }}>
        <Box sx={{ m: -1.5 }}>

        <Grid
            container
            spacing={3}
          >
          <Grid
              xs={12}
              md={3}
            >
          <Box sx={{
            textAlign : 'center'
          }}>
            <ImageInput totalImage={2} onChange={onSubmit} avtar = {values.icon}/>
          </Box>
            </Grid>
          {/* CATEGORY NAME */}

            <Grid
              xs={12}
              md={6}
            >
              <TextField
                sx={{mt:3}}
                fullWidth
                label="Category Name"
                name="category"
                onChange={handleChange}
                required={true}
                value={values.category}
                InputLabelProps={{shrink :true}}
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


UpdateStoreCategory.getLayout = (page) => (
<DashboardLayout>
  {page}
</DashboardLayout>
);

export default UpdateStoreCategory;