import { Alert, Box, Snackbar, Stack } from '@mui/material';
import axios from 'axios';
import Head from 'next/head';
import { useCallback, useEffect, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { BasicHeaders } from 'src/sections/basic-header';
import { BasicSearch } from 'src/sections/basic-search';
import { PlanSearch } from 'src/sections/plans/plans-search';
import { ServicePlansHeaders } from 'src/sections/services/service-plans-header';
import { ServicePlansTable } from 'src/sections/services/service-plans-table';
import { host } from 'src/utils/util';

const Page = () => {
    const [servicePlans, setServicePlans] = useState([]);
    const [flag, setFlag] = useState('warning');
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState('');
    const auth = useAuth();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [data, setData] = useState({
        pageNumber: page,
        size: rowsPerPage
    });
    const [totalElements, setTotalElements] = useState(0)

    useEffect(() => {
        axios.defaults.headers = {
            Authorization: auth.token,
        };

        // Get all permission
        axios
            .post(host + '/admin/plans/service-plans', data)
            .then((res) => {
                let plansList = res.data.content;
                setServicePlans(plansList);
                setTotalElements(res.data.totalElements)
            })
            .catch((err) => {
                setFlag('error');
                setMessage(!!err.response ? err.response.data.message : err.message);
                setOpen(true);
            });
        // end here.
    }, [data, page, rowsPerPage]);


    const updateStatusOnUi = (status, slug) => {
        setServicePlans((plans) => {
            plans.filter((_, index) => {
            if (_.slug === slug) return _.status = status
            return _;
          })
          return plans
        });
      }
    

    const onStatusChange = (slug, status) => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        axios.post(host + `/admin/plans/status`, {
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
                updateStatusOnUi(status, slug)
                setOpen(true)
            }).catch(err => {
                console.log(err)
                setFlag("error")
                setMessage(!!err.response ? err.response.data.message : err.message)
                setOpen(true)
            })
    }




    const onDelete = (slug) => {
        axios.defaults.headers = {
            Authorization: auth.token
        }
        axios.post(`${host}/admin/plans/delete`,
            {
                slug: slug
            }
        )
            .then(res => {
                setFlag("success")
                setMessage(res.data.message)
                setOpen(true)
                setServicePlans((servicePlans) => servicePlans.filter((servicePlan) => servicePlan.slug !== slug));
            }).catch(err => {
                console.log(err)
                setFlag("error")
                setMessage(!!err.response ? err.response.data.message : err.message)
                setOpen(true)
            })
    }


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


    const handleClose = () => {
        setOpen(false);
    }

    const onSearch = (searchData) => {
        if (!!searchData) {
            setData({
                ...data,
                ...searchData,
            })
        } else {
            setData({
                pageNumber: page,
                size: rowsPerPage
            })
        }
    }
    return (
        <>
            <Snackbar
                anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
                open={open}
                onClose={handleClose}
                key={'top' + 'right'}
            >
                <Alert onClose={handleClose} severity={flag} sx={{ width: '100%' }}>
                    {message}
                </Alert>
            </Snackbar>
            <Head>
                <title>Plans</title>
            </Head>
            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    py: 8,
                }}
            >
                <Box
                    sx={{
                        margin: '0 auto',
                        width: '95%',
                    }}
                >
                    <Stack spacing={3}>
                        <ServicePlansHeaders headerTitle={'Service Plans'} userType="W" />
                        <BasicSearch onSearch={onSearch} />
                        <ServicePlansTable
                            count={totalElements}
                            servicePlans={servicePlans}
                            onPageChange={handlePageChange}
                            onRowsPerPageChange={handleRowsPerPageChange}
                            page={page}
                            rowsPerPage={rowsPerPage}
                            onStatusChange={onStatusChange}
                            onDelete={onDelete}
                        />
                    </Stack>
                </Box>
            </Box>
        </>
    );
};

Page.getLayout = (page) => <DashboardLayout>{page}</DashboardLayout>;

export default Page;