import CurrencyRupeeIcon from '@mui/icons-material/CurrencyRupee';
import DiscountIcon from '@mui/icons-material/Discount';
import {
  Badge,
  Box,
  Card,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TablePagination,
  TableRow,
  Typography,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle
} from '@mui/material';
import { format } from 'date-fns';
import PropTypes from 'prop-types';
import { CopyOutlined } from '@ant-design/icons';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useEffect, useState } from 'react';
import { rowsPerPageOptions, toTitleCase } from 'src/utils/util';
const currentDateTime = new Date().getTime();
export const PlanTable = (props) => {
  const {
    count = 0,
    onPageChange = () => { },
    onRowsPerPageChange,
    page = 0,
    rowsPerPage = 0,
    selected = []
  } = props;
  const [plans, setPlans] = useState(props.plans);
  const [message, setMessage] = useState("");
  const [confirm, setConfirm] = useState(false);
  const [slug, setSlug] = useState(null);
  const [status, setStatus] = useState('');
  const [action, setAction] = useState('');
  const [isCopied, setIsCopied] = useState(false);
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('md'));


  async function copyTextToClipboard(text) {
    if ('clipboard' in navigator) {
      return await navigator.clipboard.writeText(text);
    } else {
      return document.execCommand('copy', true, text);
    }
  }

  const handleCopyClick = (slug) => {
    copyTextToClipboard(slug)
      .then(() => {
        setPlans((plans) =>
          plans.map((customer) => {
            if (customer.slug === slug) {
              customer.isCopied = true;
              setIsCopied(true);
            }
            return customer;
          })
        );

        setTimeout(() => {
          setPlans((plans) =>
            plans.map((customer) => {
              if (customer.slug === slug) {
                customer.isCopied = false;
                setIsCopied(false);
              }
              return customer;
            })
          );
        }, 1500);
      })
      .catch((err) => {
        console.log(err);
      });
  };

  useEffect(() => {
    setPlans(props.plans);
  }, [props.plans]);

  const handleClose = () => {
    setConfirm(false);
  };
  const confirmBox = () => {
    setConfirm(true);
  };

  const takeAction = (action) => {
    if (action === 'delete') {
      props.onDelete(slug);
    } else if (action == 'status') {
      props.onStatusChange(slug, status);
    } else if (action == 'stock') {
      props.onChangeInStock(slug, status);
    }
    setConfirm(false);
  };
  return (
    <>
      <Card sx={{ overflowX: 'auto' }}>
        <Box sx={{ minWidth: 800 }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell padding="checkbox"></TableCell>
                <TableCell>Plan Name</TableCell>
                <TableCell>Token</TableCell>
                <TableCell>Months</TableCell>
                <TableCell>Plan Price</TableCell>
                <TableCell>Discount</TableCell>
                <TableCell>Discount%</TableCell>
                <TableCell>Paid Price</TableCell>
                <TableCell>Created At</TableCell>
                <TableCell>Expiry Date</TableCell>
                <TableCell align='center'>Status</TableCell>
                <TableCell align='center'>Paid Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {plans.map((plan, index) => {
                const isSelected = selected.includes(plan.slug);
                const createdAt = format(plan.createdAt, 'dd/MM/yyyy');
                const expiryDate = format(plan.expiryDate, 'dd/MM/yyyy');
                return (
                  <TableRow hover key={plan.id} selected={isSelected}>
                    <TableCell padding="checkbox"></TableCell>

                    {/* plan name */}
                    <TableCell>
                      <Typography variant="subtitle2">
                        {toTitleCase(plan.servicePlan?.name)}
                      </Typography>
                    </TableCell>

                    {/* token */}
                    <TableCell sx={{ color: 'text.secondary' }}>
                      <span style={{ color: 'green' }}>{plan.slug}</span>
                      {!!plan.isCopied && plan.isCopied && isCopied ? (
                        <Badge
                          color="primary"
                          badgeContent="copied"
                          style={{ marginBottom: '35px' }}
                        />
                      ) : (
                        <></>
                      )}
                      <CopyOutlined onClick={() => { handleCopyClick(plan.slug) }} />
                    </TableCell>

                    {/* Months */}
                    <TableCell>
                      <Typography variant="subtitle2">
                        {plan.servicePlan?.months}
                      </Typography>
                    </TableCell>

                    {/* plan price */}
                    <TableCell>
                      <Stack alignItems="center" direction="row" spacing={2}>
                        {plan.servicePlan?.price}
                        <CurrencyRupeeIcon sx={{ fontSize: '15px', mt: '20px' }} />
                      </Stack>
                    </TableCell>

                    {/* discount */}
                    <TableCell>
                      <Stack alignItems="center" direction="row" spacing={2}>
                        <span>{plan.servicePlan?.discount} </span>
                        <CurrencyRupeeIcon sx={{ fontSize: '15px', mt: '20px' }} />
                        <DiscountIcon sx={{ color: 'red', fontSize: '20px', mt: '20px', px: '0px' }} />
                      </Stack>
                    </TableCell>

                    {/* discount % */}
                    <TableCell>
                      <Stack alignItems="center" direction="row" spacing={2}>
                        <span
                          style={{
                            color: 'green',
                            fontWeight: 'bold',
                          }}
                        >

                          {plan.servicePlan?.price > 0 ? Math.floor((plan.servicePlan?.discount / plan.servicePlan?.price) * 100) : 0 + '%'}
                        </span>
                      </Stack>
                    </TableCell>

                    {/* paid price */}
                    <TableCell>
                      <Stack alignItems="center" direction="row" spacing={2}>
                        {plan.servicePlan?.price - plan.servicePlan?.discount}
                        <CurrencyRupeeIcon sx={{ fontSize: '15px', mt: '20px' }} />
                      </Stack>
                    </TableCell>

                    {/* created at */}
                    <TableCell>{createdAt}</TableCell>

                    {/* expiry date */}
                    <TableCell>{expiryDate}</TableCell>

                    {/* status */}
                    <TableCell align={'center'}>
                      {plan.expiryDate >= currentDateTime ? (
                        <Badge color="success" badgeContent={'Active'} />
                      ) : (
                        <Badge color="error" badgeContent={'Expired'} />
                      )}
                    </TableCell>

                    {/* paid status */}
                    <TableCell align='center'>
                      {/* <Stack alignItems="center" direction="row" spacing={2}> */}
                      {plan.servicePlan?.price < 1 && (
                        <Badge color="error" badgeContent={'Free'} />
                      )}
                      {plan.servicePlan?.price > 0 && (
                        <Badge color="success" badgeContent={'Paid'} />
                      )}
                      {/* </Stack> */}
                    </TableCell>
                  </TableRow>

                );
              })}
            </TableBody>
          </Table>
        </Box>
        <TablePagination
          component="div"
          count={count}
          onPageChange={onPageChange}
          onRowsPerPageChange={onRowsPerPageChange}
          page={page}
          rowsPerPage={!!rowsPerPage ? rowsPerPage : rowsPerPageOptions[0]}
          rowsPerPageOptions={rowsPerPageOptions}
        />
      </Card>

      <Dialog
        fullScreen={fullScreen}
        open={confirm}
        onClose={handleClose}
        aria-labelledby="responsive-dialog-title"
      >
        <DialogTitle id="responsive-dialog-title">
          {'Are you sure ?'}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>{message}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button autoFocus onClick={handleClose}>
            Disagree
          </Button>
          <Button onClick={() => takeAction(action)} autoFocus>
            Agree
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

PlanTable.propTypes = {
  count: PropTypes.number,
  plans: PropTypes.array,
  onDeselectAll: PropTypes.func,
  onDeselectOne: PropTypes.func,
  onPageChange: PropTypes.func,
  onRowsPerPageChange: PropTypes.func,
  onSelectAll: PropTypes.func,
  onSelectOne: PropTypes.func,
  page: PropTypes.number,
  rowsPerPage: PropTypes.number,
  selected: PropTypes.array,
};