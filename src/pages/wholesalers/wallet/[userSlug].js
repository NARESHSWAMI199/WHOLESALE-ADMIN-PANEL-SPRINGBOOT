import { Alert, Box, Button, Card, CardActions, CardContent, CardHeader, Container, Divider, Unstable_Grid2 as Grid, InputAdornment, Snackbar, Stack, TextField, Typography } from "@mui/material";
import axios from "axios";
import { useSearchParams } from "next/navigation";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from 'src/hooks/use-auth';
import { useSelection } from "src/hooks/use-selection";
import { Layout as DashboardLayout } from 'src/layouts/dashboard/layout';
import { WalletTransactions } from "src/sections/wholesale/wallet-transaction";
import { host, rowsPerPageOptions } from 'src/utils/util';

const Page = () => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState("");
  const [flag, setFlag] = useState("success");
  const auth = useAuth();
  const [values, setValues] = useState({
    amount: "",
  });
  const [transactions, setTransactions] = useState([]);

  const paginations = auth.paginations
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(paginations?.WALLETTRANSACTIONS?.rowsNumber);
  const itemsSelection = useSelection();
  const [totalElements, setTotalElements] = useState(0)
  const searchParams = useSearchParams();
  const slug = searchParams.get('userSlug');
  const [walletAmount, setWalletAmount] = useState(0);

   const [data, setData] = useState({
          pageNumber: page,
          size: !!rowsPerPage ? parseInt(rowsPerPage) : rowsPerPageOptions[0]
      })
        

  const handleChange = useCallback((event) => {
    setValues((prevState) => ({
      ...prevState,
      [event.target.name]: event.target.value
    }));
  }, []);


  useEffect(() => {
    axios.defaults.headers = {
      Authorization: auth.token
    } 
    axios.get(`${host}/admin/store/wallet/${slug}`)
      .then(res => {
        const data = res.data;
        setWalletAmount(data.amount)
      })
      .catch(err => {
        console.log(err);
        setMessage(!!err.response ? err.response?.data.message : err.message);
        setFlag("error");
        setOpen(true);
      });
  }, [slug]);



  // Fetch transactions
  useEffect(() => {
    axios.defaults.headers = {
      Authorization: auth.token
    } 
    axios.post(`${host}/admin/store/wallet/transactions/all/${slug}`,data)
      .then(res => {
        const data = res.data.content;
        setTransactions(data);
        setTotalElements(res.data.totalElements);
      })
      .catch(err => {
        console.log(err);
        setMessage(!!err.response ? err.response?.data.message : err.message);
        setFlag("error");
        setOpen(true);
      });
  },[data,rowsPerPage, page]);



  const handleSubmit = (e) => {
    e.preventDefault();
    if (!values.amount) {
      setMessage("Please fill in all fields");
      setFlag("error");
      setOpen(true);
      return;
    }
    
  };

  const handleClose = useCallback(() => {
    setOpen(false);
  });


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



  return (
    <Box component="main" sx={{ flexGrow: 1, py: 8 }}>
      <Container maxWidth="xl">
        <Stack spacing={3}>
          <Grid xs={12} md={6} lg={8}>
            <Typography variant="h6">
                Current Wallet Balance: {!!walletAmount ? walletAmount : 0}
            </Typography>
          <Box>
            <Card sx={{ mt: 3 }}>
              <CardHeader title="Wallet Transactions" />
              <CardContent>
                <Box sx={{ minWidth: 800 }}>
                  <WalletTransactions  
                      transactions={transactions}
                      count={totalElements}
                      onPageChange={handlePageChange}
                      onRowsPerPageChange={handleRowsPerPageChange}
                      page={page}
                      rowsPerPage={rowsPerPage}
                      selected={itemsSelection.selected}
                  />
                </Box>
              </CardContent>
            </Card>
          </Box>

          </Grid>
          <Snackbar anchorOrigin={{ vertical: 'top', horizontal: 'right' }} open={open} onClose={handleClose} key={'top' + 'right'}>
            <Alert onClose={handleClose} severity={flag} sx={{ width: '100%' }}>
              {message}
            </Alert>
          </Snackbar>
        </Stack>
      </Container>
    </Box>
  );
};

Page.getLayout = (page) => (
  <DashboardLayout walletUpdate={true}>
    {page}
  </DashboardLayout>
);

export default Page;