import CancelIcon from '@mui/icons-material/Cancel';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import DeleteIcon from '@mui/icons-material/Delete';
import {
  Avatar,
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
  Typography
} from '@mui/material';
import { format } from 'date-fns';
import PropTypes from 'prop-types';

import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';

import { CopyOutlined } from '@ant-design/icons';
import { AccountBalanceWalletOutlined } from '@mui/icons-material';
import AccessibilityNewIcon from '@mui/icons-material/AccessibilityNew';
import EditIcon from '@mui/icons-material/Edit';
import { Image } from 'antd';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';
import { getInitials } from 'src/utils/get-initials';
import { rowsPerPageOptions, toTitleCase, userImage } from 'src/utils/util';

export const CustomersTable = (props) => {
  const {
    count = 0,
    onDeselectAll,
    onDeselectOne,
    onPageChange = () => { },
    onRowsPerPageChange,
    onSelectAll,
    onSelectOne,
    page = 0,
    rowsPerPage = 0,
    selected = []
  } = props;
  const [items, setItems] = useState(props.items)
  const [message, setMessage] = useState("")
  const selectedSome = (selected.length > 0) && (selected.length < items.length);
  const selectedAll = (items.length > 0) && (selected.length === items.length);
  const [confirm, setConfirm] = useState(false)
  const [slug, setSlug] = useState(null)
  const [rowIndex, setRowIndex] = useState(-1)
  const [status, setStatus] = useState('')
  const [action, setAction] = useState('')
  const [isCopied, setIsCopied] = useState(false);
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('md'));
  const auth = useAuth()
  const user = auth.user

  const changeStatus = (slug, status) => {
    props.onStatusChange(slug, status)
  }


  useEffect(() => {
    setItems((props.items).filter(customer => customer.slug != user.slug))
  }, [props.items])



  const handleClose = () => {
    setConfirm(false)
  }
  const confirmBox = () => {
    setConfirm(true)
  };

  const takeAction = (action) => {
    if (action === 'delete') {
      //setItems((items) =>items.filter((_, index) => index !== rowIndex));
      props.onDelete(slug)
    } else if (action == 'status') {
      changeStatus(slug, status)
    }
    setConfirm(false)
  }


  async function copyTextToClipboard(text) {
    if ('clipboard' in navigator) {
      return await navigator.clipboard.writeText(text);
    } else {
      return document.execCommand('copy', true, text);
    }
  }

  const handleCopyClick = (slug) => {
    // Asynchronously call copyTextToClipboard
    copyTextToClipboard(slug)
      .then(() => {
        // If successful, update the isCopied state value
        setItems((items).filter(customer => {
          if (customer.slug == slug) {
            customer.isCopied = true
            setIsCopied(true);
          }
          return customer
        }))
        setTimeout(() => {
          setItems((items).filter(customer => {
            if (customer.slug == slug) {
              customer.isCopied = false
              setIsCopied(false);
            }
            return customer
          }))
        }, 1500);
      })
      .catch((err) => {
        console.log(err);
      });
  }


  return (<>
    <Card sx={{ overflowX: 'auto' }}>
      <Box sx={{ minWidth: 800 }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell padding="checkbox">
              </TableCell>
              <TableCell>Name</TableCell>
              <TableCell>TOKEN ID</TableCell>
              <TableCell>USER TYPE</TableCell>
              <TableCell>Email</TableCell>
              {/* <TableCell>Location</TableCell> */}
              <TableCell>Phone</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Signed Up</TableCell>
              <TableCell>ACTIONS</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {items.map((customer, index) => {
              const isSelected = selected.includes(customer.id);
              const createdAt = format(customer.createdAt, 'dd/MM/yyyy');

              return (
                <TableRow
                  hover
                  key={customer.id}
                  selected={isSelected}
                >
                  <TableCell padding="checkbox"></TableCell>

                  {/*  customer name and avtar */}
                  <TableCell>
                    <Stack
                      alignItems="center"
                      direction="row"
                      spacing={2}
                    >
                      {!!customer.avatar ? <Image style={{ borderRadius: '50%', height: '50px', width: '50px' }} src={userImage + customer.slug + "/" + customer.avatar} /> :

                        <Avatar src={userImage + customer.avatar} >
                          {getInitials(customer.username)}
                        </Avatar>
                      }

                      <Link
                        href={{
                          pathname: '/account/[slug]',
                          query: { slug: customer.slug },
                        }}
                        style={{ textDecoration: 'none' }}
                      >
                        <Typography title="Edit Profile" variant="subtitle2" sx={{ color: "text.primary" }} >
                          {toTitleCase(customer.username)}
                        </Typography>
                      </Link>
                    </Stack>
                  </TableCell>
                  {/* Token */}
                  <TableCell sx={{ color: 'text.secondary' }}>
                    <span style={{ color: 'green' }}>{customer.slug} </span>
                    {!!customer.isCopied && customer.isCopied && isCopied ? <Badge color="primary" badgeContent="copied" style={{ marginBottom: '35px' }} /> : <></>}
                    <CopyOutlined onClick={() => { handleCopyClick(customer.slug) }} />
                  </TableCell>
                  {/* user type */}
                  <TableCell>
                    {/* Facing issue with badges position so that's why se given  */}
                    <Box sx={{px : '28px'}}>
                    {customer.userType === "R" && <Badge color="error" badgeContent={'Retailer'} sx={{}} />}
                    {customer.userType === "SA" && <Badge color='warning' badgeContent={'Admin'} />}
                    {customer.userType === "W" &&
                      <Link
                        href={{
                          pathname: '/store/[userSlug]',
                          query: { userSlug: customer.slug },
                        }}
                      >
                        <Badge color="info" title="Detailed View Of Store" badgeContent={'Wholesaler'} />
                      </Link>
                    }

                    {customer.userType === "S" && <Badge color="primary" badgeContent={'Staff'} />}
                    </Box>
                  </TableCell>
                  {/* email */}
                  <TableCell>
                    {customer.email}
                  </TableCell>
                  {/* <TableCell>
                      {customer.address.city}, {customer.address.state}, {customer.address.country}
                    </TableCell> */}
                  {/* contact */}
                  <TableCell>
                    {customer.contact}
                  </TableCell>
                  {/* status */}
                  <TableCell>
                    {/* {setStatus(customer.status)} */}
                    {customer.status !== 'A' ? <CancelIcon sx={{
                      marginX: '2px',
                      color: 'Red'

                    }} titleAccess='activate' onClick={(e) => {
                      setMessage("We are going to activate this user.")
                      setSlug(customer.slug)
                      setStatus('A')
                      setAction('status')
                      confirmBox()
                    }} />

                      :
                      <CheckCircleIcon sx={{
                        marginX: '2px',
                        color: 'Green'

                      }} titleAccess='deactivate' onClick={(e) => {
                        setMessage("We are going to deactivate this user.")
                        setSlug(customer.slug)
                        setStatus('D')
                        setAction('status')
                        confirmBox()
                      }} />
                    }
                  </TableCell>

                  {/* created at */}
                  <TableCell>
                    {createdAt}
                  </TableCell>

                  {/* Actions */}
                  <TableCell>

                    <Link
                      href={{
                        pathname: '/account/[slug]',
                        query: { slug: customer.slug },
                      }}
                    >
                      <EditIcon sx={{
                        marginX: '5px',
                        color: '#111927'
                      }}
                        titleAccess='Edit'
                      />
                    </Link>
                    {customer.userType == "W" &&
                      <>
                        <Link
                          href={{
                            pathname: '/wholesalers/permissions/[slug]',
                            query: { slug: customer.slug },
                          }}
                        >
                          <AccessibilityNewIcon sx={{
                            marginX: '5px',
                            color: '#111927'
                          }}
                            titleAccess='Permissions'
                          />
                        </Link>

                        <Link
                          href={{
                            pathname: '/wholesalers/plans/[userSlug]',
                            query: { userSlug: customer.slug },
                          }}
                        >
                          <AccountBalanceWalletOutlined sx={{
                            marginX: '5px',
                            color: '#111927'
                          }}
                            titleAccess='Plans'
                          />
                        </Link>
                      </>
                    }
                    <DeleteIcon sx={{
                      marginX: '5px',
                      color: 'Red'

                    }} titleAccess='delete' onClick={(e) => {
                      setSlug(customer.slug)
                      setRowIndex(index)
                      setMessage("We are going to delete this user if user type is wholesaler then user's store will also delete. if you agree press agree otherwise press disagree.")
                      setAction("delete")
                      confirmBox()
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
        rowsPerPage={rowsPerPage}
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

CustomersTable.propTypes = {
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
