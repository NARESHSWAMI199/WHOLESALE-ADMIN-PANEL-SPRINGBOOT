import { Alert, Box, Container, Snackbar, Stack } from '@mui/material';
import axios from 'axios';
import Head from 'next/head';
import { useCallback, useEffect, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { BasicHeaders } from 'src/sections/basic-header';
import { PlanSearch } from 'src/sections/plans/plans-search';
import { PlanTable } from 'src/sections/plans/plans-table';
import { host } from 'src/utils/util';

const Page = () => {
    const [plans, setPlans] = useState([]);
    const [flag, setFlag] = useState('warning');
    const [open, setOpen] = useState(false);
    const [message, setMessage] = useState('');
    const auth = useAuth();
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [data, setData] = useState({
        userType: 'W',
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
            .post(host + '/admin/plans/user-plans',data)
            .then((res) => {
                let plansList = res.data.content;
                setPlans(plansList);
                setTotalElements(res.data.totalElements)
            })
            .catch((err) => {
                setFlag('error');
                setMessage(!!err.response ? err.response.data.message : err.message);
                setOpen(true);
            });
        // end here.
    }, [data, page, rowsPerPage]);


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
                userType: "W"
            })
        } else {
            setData({
                userType: "W",
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
                <title>Sold Plans</title>
            </Head>
            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    py: 8,
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
                        <BasicHeaders headerTitle={'Sold Plans'} userType="W" />
                        <PlanSearch onSearch={onSearch} />
                        <PlanTable
                            count={totalElements}
                            plans={plans}
                            onPageChange={handlePageChange}
                            onRowsPerPageChange={handleRowsPerPageChange}
                            page={page}
                            rowsPerPage={rowsPerPage}
                        />
                    </Stack>
                </Container>
            </Box>
        </>
    );
};

Page.getLayout = (page) => <DashboardLayout>{page}</DashboardLayout>;

export default Page;