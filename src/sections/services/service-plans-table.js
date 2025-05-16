import CancelIcon from '@mui/icons-material/Cancel';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CurrencyRupeeIcon from '@mui/icons-material/CurrencyRupee';
import DeleteIcon from '@mui/icons-material/Delete';
import DiscountIcon from '@mui/icons-material/Discount';
import {
    Box,
    Button,
    Card,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TablePagination,
    TableRow,
    Typography,
    useMediaQuery,
    useTheme
} from '@mui/material';
import { format } from 'date-fns';
import PropTypes from 'prop-types';
import { useEffect, useState } from 'react';
import CopyButton from 'src/components/CopyButton';
import { rowsPerPageOptions, toTitleCase } from 'src/utils/util';

export const ServicePlansTable = (props) =>{
    const {
        count = 0,
        onPageChange = () => { },
        onRowsPerPageChange,
        page = 0,
        rowsPerPage = 0,
        selected = []
    } = props;
    const [servicePlans, setServicePlans] = useState(props.servicePlans);
    const [message, setMessage] = useState("");
    const [confirm, setConfirm] = useState(false);
    const [slug, setSlug] = useState(null);
    const [rowIndex, setRowIndex] = useState(-1);
    const [status, setStatus] = useState('');
    const [action, setAction] = useState('');
    const theme = useTheme();
    const fullScreen = useMediaQuery(theme.breakpoints.down('md'));

    useEffect(() => {
        setServicePlans(props.servicePlans)
    }, [props.servicePlans]);

    const handleClose = () => {
        setConfirm(false);
    };

    const confirmBox = () => {
        setConfirm(true);
    };

    const takeAction = (action) => {
        if (action === 'delete') {
            props.onDelete(slug);
        } else if (action === 'status') {
            props.onStatusChange(slug, status);
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
                                <TableCell>TOKEN ID</TableCell>
                                <TableCell>Months</TableCell>
                                <TableCell>Price</TableCell>
                                <TableCell>Discount</TableCell>
                                <TableCell>Discount %</TableCell>
                                <TableCell>Total price</TableCell>
                                <TableCell>Status</TableCell>
                                <TableCell>Created At</TableCell>
                                <TableCell>Updated At</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {servicePlans.map((plan, index) => {
                                const isSelected = selected.includes(plan.id);
                                const createdAt = format(plan.createdAt, 'dd/MM/yyyy');
                                return (
                                    <TableRow hover key={plan.id} selected={isSelected}>
                                        <TableCell padding="checkbox"></TableCell>

                                        {/* Plan Name */}
                                        <TableCell>
                                            <Stack alignItems="center" direction="row" spacing={2}>
                                                <Typography title="Edit Profile" variant="subtitle2" sx={{ color: 'text.primary' }}>
                                                    {toTitleCase(plan.name)}
                                                </Typography>
                                            </Stack>
                                        </TableCell>
                                        {/* Token */}
                                        <TableCell sx={{ color: 'text.secondary' }}>
                                            <span style={{ color: 'green' }}>{plan.slug} </span>
                                            <CopyButton text={plan.slug} />
                                        </TableCell>
                                        
                                        {/* Months */}
                                        <TableCell>
                                            <Stack alignItems="center" direction="row" spacing={2}>
                                                {plan.months}
                                            </Stack>
                                        </TableCell>


                                        {/* plan price */}
                                        <TableCell>
                                            <Stack alignItems="center" direction="row" spacing={2}>
                                                {plan.price}
                                                <CurrencyRupeeIcon sx={{ fontSize: '15px', mt: '20px' }} />
                                            </Stack>
                                        </TableCell>

                                        {/* discount */}
                                        <TableCell>
                                            <Stack alignItems="center" direction="row" spacing={2}>
                                                <span>{plan.discount} </span>
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
                                                    {(plan.price > 0 ? Math.floor((plan.discount / plan.price) * 100) : 0) + '%'}
                                                </span>
                                            </Stack>
                                        </TableCell>

                                        {/* Total price */}
                                        <TableCell>
                                            <Stack alignItems="center" direction="row" spacing={2}>
                                                {plan.price - plan.discount}
                                                <CurrencyRupeeIcon sx={{ fontSize: '15px', mt: '20px' }} />
                                            </Stack>
                                        </TableCell>

                                        {/* actions */}
                                        <TableCell>
                                            {plan.status !== 'A' ? (
                                                <CancelIcon sx={{ marginX: '2px', color: 'Red' }} titleAccess='activate' onClick={() => {
                                                    setMessage("We are going to activate this service plan.");
                                                    setSlug(plan.slug);
                                                    setStatus('A');
                                                    setAction('status');
                                                    confirmBox();
                                                }} />
                                            ) : (
                                                <CheckCircleIcon sx={{ marginX: '2px', color: 'Green' }} titleAccess='deactivate' onClick={() => {
                                                    setMessage("We are going to deactivate this service plan.");
                                                    setSlug(plan.slug);
                                                    setStatus('D');
                                                    setAction('status');
                                                    confirmBox();
                                                }} />
                                            )}
                                        </TableCell>
                                        <TableCell>{createdAt}</TableCell>
                                        <TableCell>
                                            {/* <Link href={{}}>
                                                <EditIcon sx={{ marginX: '5px', color: '#111927' }} titleAccess='Edit' />
                                            </Link> */}

                                            <DeleteIcon sx={{ marginX: '5px', color: 'Red' }} titleAccess='delete' onClick={() => {
                                                setSlug(plan.slug);
                                                setRowIndex(index);
                                                setMessage("We are going to delete this service plan. If you agree press agree otherwise press disagree.");
                                                setAction("delete");
                                                confirmBox();
                                            }} />
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
                    {"Are you sure ?"}
                </DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        {message}
                    </DialogContentText>
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

ServicePlansTable.propTypes = {
    count: PropTypes.number,
    items: PropTypes.array,
    onDeselectAll: PropTypes.func,
    onDeselectOne: PropTypes.func,
    onPageChange: PropTypes.func,
    onRowsPerPageChange: PropTypes.func,
    onSelectAll: PropTypes.func,
    onSelectOne: PropTypes.func,
    page: PropTypes.number,
    rowsPerPage: PropTypes.number,
    selected: PropTypes.array
};
