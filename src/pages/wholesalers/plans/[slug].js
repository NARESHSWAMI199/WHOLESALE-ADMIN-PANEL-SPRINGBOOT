import { Alert, Box, Snackbar, Stack } from '@mui/material';
import axios from 'axios';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
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
  const router = useRouter();
  const { slug } = router.query;
  const auth = useAuth();

  useEffect(() => {
    axios.defaults.headers = {
      Authorization: auth.token,
    };

    // Get all permission
    axios
      .post(host + '/admin/plans/user-plans/' + slug,{})
      .then((res) => {
        let plansList = res.data.content;
        setPlans(plansList);
      })
      .catch((err) => {
        setFlag('error');
        setMessage(!!err.response ? err.response.data.message : err.message);
        setOpen(true);
      });
    // end here.
  }, [slug, auth.token]);

  const handleClose = () => {
    setOpen(false);
  }

  const onSearch = (searchData) => {
    if(!!searchData){
    setData({
      ...data,
      ...searchData,
      userType : "W"
    })
  }else {
    setData({
      slug : slug,
      userType : "W",
      pageNumber : page,
      size : rowsPerPage
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
        <title>Wholesaler | Swami Sales</title>
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
            <BasicHeaders headerTitle={'Plans'} userType="W" />
            <PlanSearch onSearch={onSearch} />
            <PlanTable plans={plans} />
          </Stack>
        </Box>
      </Box>
    </>
  );
};

Page.getLayout = (page) => <DashboardLayout>{page}</DashboardLayout>;

export default Page;