import ArrowDownOnSquareIcon from '@heroicons/react/24/solid/ArrowDownOnSquareIcon';
import PlusIcon from '@heroicons/react/24/solid/PlusIcon';
import { Alert, Box, Button, Container, Snackbar, Stack, SvgIcon } from '@mui/material';
import axios from 'axios';
import Head from 'next/head';
import Link from 'next/link';
import { useRouter } from 'next/router';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';
import { useSelection } from 'src/hooks/use-selection';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import DialogFormForExcelImport from 'src/layouts/excel/import-excel';
import { BasicSearch } from 'src/sections/basic-search';
import { StoresCard } from 'src/sections/wholesale/stores-table';
import { ItemsTable } from 'src/sections/wholesale/item-table';
import { host, rowsPerPageOptions, toTitleCase } from 'src/utils/util';



const UseitemSlugs = (items) => {
    return useMemo(
        () => {
            return items.map((item) => item.slug);
        },
        [items]
    );
};


const Page = () => {


    /** snackbar varibatles */

    const router = useRouter()
    const { userSlug } = router.query

    const [open, setOpen] = useState()
    const [message, setMessage] = useState("")
    const [flag, setFlag] = useState("warning")

    const auth = useAuth()
    const paginations = auth.paginations
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(paginations?.ITEMS?.rowsNumber);
    const [items, setItems] = useState([])
    const itemSlugs = UseitemSlugs(items);
    const itemsSelection = useSelection(itemSlugs);
    const [totalElements, setTotalElements] = useState(0)
    const [wholesale, setWholesale] = useState({})
    
    const [data, setData] = useState({
        pageNumber: page,
       size: !!rowsPerPage ? rowsPerPage : rowsPerPageOptions[0]
    })


    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [downloadUrl, setDownloadUrl] = useState('');

    useEffect(()=>{
        setData((previous)=>({...previous , storeId : wholesale.id}))
    },[wholesale])



    /** Get wholesale using user slug. */
    useEffect(() => {
        const updateStoreId = async () => {
            axios.defaults.headers = {
                Authorization: auth.token
            }
            await axios.get(host + '/admin/store/detailbyuser/' + userSlug)
                .then(res => {
                    let store = res.data.res;
                    setWholesale(store)
                })
                .catch(err => {
                    setMessage(!!err.response ? err.response.data.message : err.message)
                    setFlag('error')
                    setOpen(true)
                })
        }
        updateStoreId()
    }, [])



    


    useEffect(() => {
        if(data.storeId === undefined || data.storeId === null) return;
        const getData = async () => {
            axios.defaults.headers = {
                Authorization: auth.token
            }
            await axios.post(host + "/admin/item/all", data)
                .then(res => {
                    const data = res.data.content;
                    setTotalElements(res.data.totalElements)
                    setItems(data);
                })
                .catch(err => {
                    console.log(err)
                    setMessage(!!err.response ? err.response.data.message : err.message)
                    setFlag("error")
                    setOpen(true)
                })
        }
        getData();

    }, [data, page, rowsPerPage])



    const importItemExcelSheet = async (formData) => {
        let success = false;
        axios.defaults.headers = {
          Authorization: auth.token
        };
        await axios.post(host + '/admin/item/importExcel/' + wholesale.slug, formData,{
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        }).then(res => {
            let  resData = res.data;
            setMessage(resData.message);
            setFlag("success");
            if(res.status === 201) {
                let fileUrl = resData.fileUrl;
                axios.get(fileUrl, { responseType: 'blob' })
                .then(response => {
                    const url = window.URL.createObjectURL(new Blob([response.data], 
                        { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }));
                    setDownloadUrl(url);
                })
                .catch(err => {
                    console.log(err);
                    setMessage("woops! something went wrong during excel file opening.");
                    setFlag("error");
                    setOpen(true);
                });
                setFlag("warning");
                setSnackbarOpen(true);
            }          
          success = true;
        })
        .catch(err => {
            console.log(err);
            setMessage(!!err.response ? err.response.data.message : err.message);
            setFlag("error");
            setOpen(true);
        });
        setOpen(true);
        return success;
      }
    

    const handleSnackbarClose = () => {
        setSnackbarOpen(false);
    };

    const handleDownload = () => {
        const link = document.createElement('a');
        link.href = downloadUrl;
        link.setAttribute('download', wholesale.storeName + '_itemNotUpdated_items.xlsx');
        document.body.appendChild(link);
        link.click();
        link.remove();
        setFlag("success")
        setMessage("Successfully dowloaded not updated items sheet.")
        setOpen(true)
        setSnackbarOpen(false);
    };

    const onStatusChange = (slug, status) => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        axios.post(host + `/admin/item/status`, {
            slug: slug,
            status: status
        })
            .then(res => {
                if (status === "A") {
                    setFlag("success")
                    setMessage("Successfully activated.")
                } else {
                    setFlag("warning")
                    setMessage("Successfully deactivated.")
                }
                changeStatus(slug,status)
                setOpen(true)
            }).catch(err => {
                console.log(err)
                setMessage(!!err.response ? err.response.data.message : err.message)
                setFlag("error")
                setOpen(true)
            })
    }


    const changeStatus = (slug,status) => {
        setItems((items) => {
            items.filter((_, index) => {
              if(_.slug === slug) return _.status = status
              return _;
            })
            return items
        });
      }



      const changeInStock = (slug,inStock) => {
        setItems((items) => {
            items.filter((_, index) => {
              if(_.slug === slug) return _.inStock = inStock
              return _;
            })
            return items
        });
      }
    


    const onChangeInStock = (slug, inStock) => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        axios.post(host + `/admin/item/stock`, {
            slug: slug,
            stock: inStock
        })
            .then(res => {
                if (inStock === "Y") {
                    setFlag("success")
                    setMessage("Successfully added in stock.")
                } else {
                    setFlag("warning")
                    setMessage("Successfully removed from stock.")
                }
                changeInStock(slug,inStock)
                setOpen(true)
            }).catch(err => {
                console.log(err)
                setMessage(!!err.response ? err.response.data.message : err.message)
                setFlag("error")
                setOpen(true)
            })
    }

    


    const onDeleteStore = (slug) => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        axios.post(`${host}/admin/store/delete`,{
            slug : slug
        })
            .then(res => {
                setFlag("success")
                setMessage(res.data.message)
                setOpen(true)
                router.push('/wholesalers');
            }).catch(err => {
                console.log(err)
                setMessage(!!err.response ? err.response.data.message : err.message)
                setFlag("error")
                setOpen(true)
            })
    }


    const onDelete = (slug) => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        axios.post(`${host}/admin/item/delete`,{
            "slug" : slug
        })
        .then(res => {
            setItems((items) =>items.filter((_) => _.slug !== slug));
            setFlag("success")
            setMessage(res.data.message)
            setOpen(true)
        }).catch(err => {
            console.log(err)
            setMessage(err.message)
            setFlag("error")
            setOpen(true)
        })
            
    }


    /** for snackbar close */
    const handleClose = () => {
        setOpen(false)
    };

    const columns = [
        { title: 'Name', dataIndex: 'name', key: 'name' },
        { title: 'Label', dataIndex: 'label', key: 'label' },
        { title: 'Slug', dataIndex: 'slug', key: 'slug' },
        { title: 'Capacity', dataIndex: 'capacity', key: 'capacity' },
        { title: 'Price', dataIndex: 'price', key: 'price' },
        { title: 'Discount', dataIndex: 'discount', key: 'discount' },
        { title: 'Stock', dataIndex: 'stock', key: 'stock' },
    ];


    const handlePageChange = useCallback(
        (event, value) => {
            setPage(value);
            setData({ ...data, pageNumber: value })
        },
        []
    );

    const handleRowsPerPageChange = useCallback(
        (event) => {
            setRowsPerPage(event.target.value);
        },
        []
    );


    const onSearch = (searchData) => {
        if(!!searchData){
        setData({
          ...data,
          ...searchData,
        })
      }else {
        setData({
            pageNumber: page,
            size: rowsPerPage
        })
      }
      } 


    const exportExcelSheet = async () => {
        const confirmDownload = window.confirm("Are you sure you want to download the Excel sheet?");
        if (!confirmDownload) {
            return;
        }

        axios.defaults.headers = {
            Authorization: auth.token
        }
        await axios.post(host + '/admin/item/exportExcel/' + wholesale.slug, {...data, size: totalElements}, { responseType: 'blob' })
            .then(response => {
                const url = window.URL.createObjectURL(new Blob([response.data], 
                    { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', wholesale.storeName + '_items.xlsx');
                document.body.appendChild(link);
                link.click();
                link.remove();
                setFlag("success")
                setMessage("Successfully exported.")
                setOpen(true)
            })
            .catch(err => {
                console.log(err)
                setMessage(!!err.response ? err.response.data.message : err.message)
                setFlag("error")
                setOpen(true)
            })
    }


    

    return (
        <>

            <Snackbar anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
                open={open}
                onClose={handleClose}
                key={'top' + 'right'}
            >
                <Alert onClose={handleClose} severity={flag} sx={{ width: '100%' }}>
                    {message}
                </Alert>
            </Snackbar>
            <Snackbar
                open={snackbarOpen}
                autoHideDuration={6000}
                onClose={handleSnackbarClose}
                message="Some items were not updated. Click to view."
                action={
                    <Button color="secondary" size="small" onClick={handleDownload}>
                        VIEW
                    </Button>
                }
            />
            <Head>
                <title>
                    {toTitleCase(wholesale.storeName)} | Swami Sales
                </title>
            </Head>
            <Box

                component="main"
                sx={{
                    flexGrow: 1,
                    py :8
                }}
            >
                 <Container maxWidth="xxl" sx={{
                    px : {
                            xs : 1,
                            sm : 1,
                            md : 1,
                            lg : 5,
                            xl : 5
                        } 
                    }}>
                    <Stack spacing={3}>

                        <StoresCard deleteStore={onDeleteStore} store={wholesale} visit={false} delete={false} />

                        <Stack
                            direction="row"
                            justifyContent="space-between"
                            spacing={4}
                        >

                            <Stack
                                alignItems="center"
                                direction="row"
                                spacing={1}
                            >
                                <DialogFormForExcelImport importExcelSheet={importItemExcelSheet} />
                                <Button
                                    color="inherit"
                                    onClick={exportExcelSheet}
                                    startIcon={(
                                        <SvgIcon fontSize="small">
                                            <ArrowDownOnSquareIcon />
                                        </SvgIcon>
                                    )}
                                >
                                    Export
                                </Button>
                            </Stack>
                            <div>
                                <Link
                                    href={{
                                        pathname: '/item/create/[slug]/[us]',
                                        query: {
                                            slug: wholesale.slug,
                                            us: userSlug
                                        },
                                    }}>
                                    <Button
                                        startIcon={(
                                            <SvgIcon fontSize="small">
                                                <PlusIcon />
                                            </SvgIcon>
                                        )}
                                        variant="contained"
                                    >
                                        Add
                                    </Button>
                                </Link>
                            </div>
                        </Stack>

                        
                    
                        <BasicSearch onSearch={onSearch} type="item" />
                        <ItemsTable
                            count={totalElements}
                            items={items}
                            onDeselectAll={itemsSelection.handleDeselectAll}
                            onDeselectOne={itemsSelection.handleDeselectOne}
                            onPageChange={handlePageChange}
                            onRowsPerPageChange={handleRowsPerPageChange}
                            onSelectAll={itemsSelection.handleSelectAll}
                            onSelectOne={itemsSelection.handleSelectOne}
                            page={page}
                            rowsPerPage={rowsPerPage}
                            selected={itemsSelection.selected}
                            onStatusChange={onStatusChange}
                            onChangeInStock={onChangeInStock}
                            onDelete={onDelete}
                        />
                    </Stack>
                </Container>
            </Box>
        </>
    );
};

Page.getLayout = (page) => (
    <DashboardLayout>
        {page}
    </DashboardLayout>
);

export default Page;
