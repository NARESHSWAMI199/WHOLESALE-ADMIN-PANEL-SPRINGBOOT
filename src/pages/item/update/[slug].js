import {
    Box,
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
    SvgIcon
} from "@mui/material";
import axios from "axios";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "src/hooks/use-auth";
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { host, itemImage } from "src/utils/util";
import { useRouter } from "next/router";
import ImageInput from "src/sections/image-input";
import RefreshIcon from '@mui/icons-material/Refresh';


const UpdateItem = () => {

    const router = useRouter()
    const { slug} = router.query

    const [open, setOpen] = useState(false)
    const [message, setMessage] = useState("")
    const [flag, setFlag] = useState("success")

    const auth = useAuth()
    const [values, setValues] = useState({})
    const [categories,setItemCategories] = useState([])
    const [subcategories,setItemSubCategories] = useState([])
  

    useEffect(() => {
        const getData = async () => {
            axios.defaults.headers = {
                Authorization: auth.token
            }
            await axios.get(host + "/admin/item/detail/"+slug,)
                .then(res => {
                    const data = res.data.res;
                    setValues({...data,category : data.itemCategory.id, subcategory : data.itemSubCategory.id})
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
        const getData = async () => {
            axios.defaults.headers = {
                Authorization: auth.token
            }
            await axios.get(host + "/admin/item/category")
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
            await axios.get(host + "/admin/item/subcategory/"+values.category)
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
        (e) => {
            e.preventDefault()
            const form = e.target;
            const formData = new FormData(form)
            let item = {
                slug: slug,
                name: formData.get("name"),
                price: formData.get("price"),
                discount: formData.get("discount"),
                inStock: formData.get("inStock") ? 'Y' : 'N',
                label: formData.get("itemLabel"),
                description: formData.get("description"),
                categoryId: formData.get("category"),
                subCategoryId: formData.get("subcategory"),
                itemImage : values.itemImage
            }

            axios.defaults.headers = {
                Authorization: auth.token,
                "Content-Type" : "multipart/form-data"
            }
            axios.post(host + "/admin/item/update", item)
                .then(res => {
                    setMessage(res.data.message)
                    setFlag("success")
                    form.reset();
                    setValues({})
                }).catch(err => {
                    setMessage(!!err.response ? err.response.data.message : err.message)
                    setFlag("error")
                })
            setOpen(true)
        })

    const handleClose = useCallback(() => {
        setOpen(false)
    })


    const onSubmit = (image) =>{
        console.log(image)
        setValues((pervious)=>({
          ...pervious,
          itemImage : image.originFileObj
        }))
      }

    return (<>

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
                            //subheader="From here you can add a new item."
                            title="Edit Item"
                        />
                        <CardContent sx={{ pt: 0 }}>
                            <Box sx={{ m: -1.5 }}>
 
                            <Box style={{marginLeft : '10px',marginTop: '10px'}}>
                                <ImageInput onChange={onSubmit} avtar={values.slug !=undefined ? itemImage+values.slug+"/"+values.avtar : ''}/>
                            </Box>

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
                                            InputLabelProps={{ shrink: true }}
                                            onChange={handleChange}
                                            required={true}
                                            value={values.name}
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
                                            value={values.price}
                                            InputLabelProps={{shrink:true}}
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
                                            value={values.discount}
                                            InputLabelProps={{ shrink: true }}
                                        />

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
                                                value={""+values.subcategory}
                                                label="Subcategory"
                                                onChange={handleChange}
                                            >
                                            {subcategories.map((subcategroyObj , i) => {
                                                return ( <MenuItem key={i} value={subcategroyObj.id}>{subcategroyObj.subcategory}</MenuItem>
                                                )})
                                            }
                                            </Select>
                                        </FormControl>
                                    </Grid>




                                    {/* Label */}
                                    <Grid
                                        xs={12}
                                        md={6}
                                    >
                                        <FormControl fullWidth>
                                            <InputLabel id="itemLabel">Label</InputLabel>
                                            <Select
                                                labelId="itemLabel"
                                                id="demo-simple-select"
                                                name='itemLabel'
                                                value={""+values.label}
                                                label="Label"
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
                                            value={values.description}
                                            rows={2}
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



        <Snackbar anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
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


UpdateItem.getLayout = (page) => (
    <DashboardLayout>
        {page}
    </DashboardLayout>
);

export default UpdateItem;