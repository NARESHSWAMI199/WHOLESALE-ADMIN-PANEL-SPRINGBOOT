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
    Container,
    Stack
} from "@mui/material";
import axios from "axios";
import { useRouter } from "next/router";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "src/hooks/use-auth";
import { ArrowButtons } from "src/layouts/arrow-button";
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { BasicHeaders } from "src/sections/basic-header";
import { host, suId } from "src/utils/util";





const Page = () =>{    

const [open,setOpen] = useState(false)
const [message,setMessage] = useState("")
const [flag,setFlag] = useState("success")
const auth = useAuth()
const user = auth.user
const[cityList,setCityList] = useState([])
const[stateList,setStateList] = useState([])
const router = useRouter()
const { createUserType } = router.query
const [userType,setUserType] = useState(createUserType)
const [values,setValues] = useState({userType : userType})
const [groups,setGroups] = useState([])
const [assignGroup , setAssignGroup] = useState([])
const [categories,setItemCategories] = useState([])
const [subcategories,setItemSubCategories] = useState([])

const [data,setData] = useState({
  pageNumber : 0,
  size : 1000000
})

useEffect(()=>{
    axios.defaults.headers={
        Authorization : auth.token
    }
    axios.get(host+"/admin/address/state")
    .then(res=>setStateList(res.data))
    .catch(err=>console.log(err))
},[])


useEffect( ()=>{
  const getData = async () => {
     axios.defaults.headers = {
       Authorization : auth.token
     }
     await axios.post(host+"/group/all",data)
     .then(res => {
        const data = res.data.content;
        if(user.id != suId || userType != 'SA'){
         setGroups(data.filter((group)=>group.id != 0));
        }else{
          setGroups(data)
        }
     })
     .catch(err => {
       //setErrors(err.message)
       setFlag("error")
       setMessage(!!err.response ? err.response.data.message : err.message)
       setOpen(true)
     } )
   }
  getData();

 },[data,userType])


useEffect(()=>{
  axios.defaults.headers={
    Authorization : auth.token
  }
  axios.get(host+`/admin/address/city/${values.state}`)
  .then(res=>{
      setCityList(res.data)}
      )
  .catch(err=>console.log(err))
},[values.state])




// const changeState = useCallback(
//       async (event) => {
//         let stateId =  event.target.value
//         setSelectedState(stateId)
//     },
//     []
//   );




  useEffect(() => {
    const getData = async () => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        await axios.post(host + "/admin/store/category",{orderBy : 'category', order : 'asc'})
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
      await axios.post(host + "/admin/store/subcategory",{categoryId : values.category , orderBy : 'subcategory', order : 'asc'})
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
    let data = {
        planName : values.planname,
        price :  values.price,
        discount : values.discount,
        months : values.months,
        description : values.description
    }

    axios.defaults.headers = {
        Authorization : auth.token
    }
    axios.post(host+"/admin/plans/add",data)
    .then(res => {
      setMessage(res.data.message)
      setFlag("success")
      form.reset();
      reset()
      setOpen(true)
    }).catch(err=>{
        console.log(err)
        setMessage(!!err.response ? err.response.data.message : err.message)
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


const handleChangeMultiple = (event) =>{
    const { options } = event.target;
    const value = [];
    for (let i = 0, l = options.length; i < l; i += 1) {
      if (options[i].selected) {
        value.push(options[i].value);
      }
    }
    setAssignGroup(value);
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
        {/* <BasicHeaders  headerTitle="Create User"  /> */}
        <Grid
            xs={12}
            md={6}
            lg={8}
          >

            <form
            autoComplete="off"
           // noValidate
            onSubmit={handleSubmit}
          >

        <Card sx={{mb:1}}>
              <CardHeader
                //subheader="From here you can add user."
                title="Create service plans"
              />
              <CardContent sx={{ pt: 0 }}>
                <Box sx={{ m: -1.5 }}>
                  <Grid
                    container
                    spacing={3}
                  >
                    <Grid
                      xs={12}
                      md={12}
                    >
                      <TextField
                        fullWidth
                        label="Plan Name"
                        name="planname"
                        onChange={handleChange}
                        required={true}
                        value={values.planName}
                      />
      
                    </Grid>
                
                    <Grid
                      xs={12}
                      md={6}
                    >
                      <TextField
                        fullWidth
                        label="Price"
                        name="price"
                        type="number"
                        onChange={handleChange}
                        required
                        value={values.price}
                      />
                    </Grid>


                    <Grid
                      xs={12}
                      md={6}
                    >
                      <TextField
                        fullWidth
                        label="Discount"
                        name="discount"
                        type="number"
                        onChange={handleChange}
                        required
                        value={values.discount}
                      />
                    </Grid>

                    <Grid
                      xs={12}
                      md={6}
                    >
                      <TextField
                        fullWidth
                        label="Months"
                        name="months"
                        type="number"
                        onChange={handleChange}
                        required
                        value={values.months}
                      />
                    </Grid>

                    <Grid
                      xs={12}
                      md={6}
                    >
                    <TextField
                        fullWidth
                        label="Description"
                        name="description"
                        type="number"
                        onChange={handleChange}
                        required
                        multiline
                        rows={4}
                        value={values.description}
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
              </CardActions>
            </Card>
          </form>
        </Grid>

  <Snackbar anchorOrigin={{ vertical : 'top', horizontal : 'right' }}
    open={open}
    onClose={handleClose}
    key={'top' + 'right'}
  >
 <Alert onClose={handleClose} severity={flag} sx={{ width: '100%' }}>
    {message}
</Alert>
</Snackbar>
</Stack>
</Container>
</Box>
    </>
)
}


Page.getLayout = (page) => (
<DashboardLayout>
  {page}
</DashboardLayout>
);

export default Page;