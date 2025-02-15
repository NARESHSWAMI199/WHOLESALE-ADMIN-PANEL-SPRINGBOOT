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
        const formData = new FormData(form)
        let store = {
            username : formData.get("username"),
            userType: formData.get("userType"),
            email: formData.get("email"),
            contact: formData.get("contact"),
            groupList : assignGroup
          }

        if (userType === "W"){
          store = {
            ...store,
            description : formData.get("description"),
            storeEmail : formData.get("storeEmail"),
            storePhone : formData.get("storePhone"),
            state:  formData.get("state"),
            city :  formData.get("city"),
            street :  formData.get("street"),
            zipCode :  formData.get("zipCode"),
            categoryId: formData.get("category"),
            subCategoryId: formData.get("subcategory"),
            storeName :  formData.get("storeName")
          }
        }

        axios.defaults.headers = {
            Authorization : auth.token
        }
        axios.post(host+"/admin/auth/add",store)
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
                    title="Create User"
                  />
                  <CardContent sx={{ pt: 0 }}>
                    <Box sx={{ m: -1.5 }}>
                      <Grid
                        container
                        spacing={3}
                      >
                        <Grid
                          xs={12}
                          md={6}
                        >
                          <TextField
                            fullWidth
                            label="Username"
                            name="username"
                            onChange={handleChange}
                            required={true}
                            value={values.username}
                          />
          
                        </Grid>
                      <Grid
                        xs={12}
                        md={6}
                      >
                        <FormControl fullWidth>
                      <InputLabel style={{background :'white'}}  id="userTypeLabel">User type</InputLabel>
                      <Select
                        labelId="demo-simple-select-label"
                        id="demo-simple-select"
                        name='userType'
                        value={values.userType}
                        onChange={(e)=>{
                          handleChange(e)
                          setUserType(e.target.value)
                        }
                        }
                      >
                        {!!user && user.userType == "SA" && 
                        <MenuItem value={"S"}>Staff</MenuItem>
                         }
                        <MenuItem value={"W"}>Wholesaler</MenuItem>
                        <MenuItem value={'R'}>Retailer</MenuItem>
                        {!!user && user.id == suId && 
                          <MenuItem value={'SA'}>Admin</MenuItem>
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
                            label="Email Address"
                            name="email"
                            type="email"
                            onChange={handleChange}
                            required
                            value={values.email}
                          />
                        </Grid>
                        <Grid
                          xs={24}
                          md={6}
                        >
                          <TextField
                            fullWidth
                            label="Phone Number"
                            name="contact"
                            onChange={handleChange}
                            type="number"
                            required
                            value={values.contact}
                          />
                        </Grid>

                      {(userType == "SA" || userType == "S") &&
                        <Grid
                          xs={12}
                          md={6}
                        >
                        <FormControl sx={{ m: 1, minWidth: 500, maxWidth: 500 }}>
                                <InputLabel shrink htmlFor="select-multiple-native">
                                  Groups
                                </InputLabel>
                                <Select
                                  multiple
                                  native
                                  name="groups"
                                  // @ts-ignore Typings are not considering `native`
                                  onChange={handleChangeMultiple}
                                  label="Native"
                                  inputProps={{
                                    id: 'select-multiple-native',
                                  }}
                                >
                                  {groups.map((group) => (
                                    <option key={group.id} value={group.id}>
                                      {group.name}
                                    </option>
                                  ))}
                                </Select>
                          </FormControl>
                        </Grid>
                      }
                      </Grid>
                    </Box>
                  </CardContent>
                  <Divider />
              

              {userType =='W' && 
                <>
                  <CardHeader
                    subheader="From here you can add store."
                    title="Store Detail"
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
                            label="Store Name"
                            name="storeName"
                            onChange={handleChange}
                            required
                            value={values.storeName}
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
                            value={values.street}
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
                            value={values.zipCode}
                            InputLabelProps={{shrink : true}}
                          />

                        </Grid>


                        <Grid
                          xs={12}
                          md={6}
                        >      
                        <FormControl fullWidth>
                        <InputLabel style={{background :'white'}}  id="stateLabel">State</InputLabel>
                          <Select
                            labelId="stateLable"
                            id="demo-simple-select"
                            name='state'
                            value={!!values.state ? values.state : ""}
                            onChange={handleChange}
                            required
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
                          <InputLabel style={{background :'white'}}  id="cityLabel">City</InputLabel>
                          <Select
                            fullWidth
                            labelId="cityLabel"
                            name='city'
                            value={!!values.city ? values.city : ""}
                            onChange={handleChange}
                            required
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
                          <InputLabel style={{background :'white'}}  id="itemLabel">Category</InputLabel>
                          <Select
                              labelId="itemLabel"
                              id="category"
                              name='category'
                              value={values.category !=undefined ? ""+values.category : ""}
                              onChange={handleChange}
                              required
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
                          <InputLabel style={{background :'white'}}  id="itemLabel">Subcategory</InputLabel>
                          <Select
                              labelId="itemLabel"
                              id="subcategory"
                              name='subcategory'
                              value={values.subcategory !=undefined ? ""+values.subcategory : ""}
                              onChange={handleChange}
                              required
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






                        <Grid
                          xs={12}
                          md={6}
                        >
                          <TextField
                            fullWidth
                            label="Store Email Address"
                            name="storeEmail"
                            onChange={handleChange}
                            required
                            value={values.storeEmail}
                          />
                        </Grid>
                        <Grid
                          xs={12}
                          md={6}
                        >
                          <TextField
                            fullWidth
                            label="Store Phone Number"
                            name="storePhone"
                            onChange={handleChange}
                            type="number"
                            required
                            value={values.storePhone}
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
                            value={values.description}
                          />
                        </Grid>
                      </Grid>
                    </Box>
                  </CardContent>
                  <Divider />
                </>
        }
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