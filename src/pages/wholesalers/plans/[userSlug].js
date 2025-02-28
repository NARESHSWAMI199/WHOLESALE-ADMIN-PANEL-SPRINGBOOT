import { Alert, Box, Snackbar, Stack } from '@mui/material';
import axios from 'axios';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useCallback, useEffect, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { BasicHeaders } from 'src/sections/basic-header';
import { PlanSearch } from 'src/sections/plans/plans-search';
import { PlanTable } from 'src/sections/plans/plans-table';
import { host, rowsPerPageOptions } from 'src/utils/util';

const Page = () => {
  const [plans, setPlans] = useState([]);
  const [flag, setFlag] = useState('warning');
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const router = useRouter();
  const { userSlug } = router.query;
  const auth = useAuth();
  const paginations = auth.paginations;
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(paginations?.WHOLESALERPLANS?.rowsNumber);
  const [data, setData] = useState({
    userType: 'W',
    pageNumber: page,
    size: !!rowsPerPage ? rowsPerPage : rowsPerPageOptions[0]
  });
  const [totalElements, setTotalElements] = useState(0)

  useEffect(() => {
    axios.defaults.headers = {
      Authorization: auth.token,
    };

    // Get all permission
    axios
      .post(host + '/admin/plans/user-plans/' + userSlug, data)
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
  }, [userSlug, data, page, rowsPerPage]);


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
        <title>Wholesaler Plans</title>
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
            <BasicHeaders headerTitle={'Wholesaler Plans'} userType="W" />
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
        </Box>
      </Box>
    </>
  );
};

Page.getLayout = (page) => <DashboardLayout>{page}</DashboardLayout>;

export default Page;