import PropTypes from 'prop-types';
import { format } from 'date-fns';
import DeleteIcon from '@mui/icons-material/Delete';
import CancelIcon from '@mui/icons-material/Cancel';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CurrencyRupeeIcon from '@mui/icons-material/CurrencyRupee';
import DiscountIcon from '@mui/icons-material/Discount';
import {
  Avatar,
  Badge,
  Box,
  Card,
  Checkbox,
  Rating,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TablePagination,
  TableRow,
  Typography
} from '@mui/material';

import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { Image } from 'antd';
import { Scrollbar } from 'src/components/scrollbar';
import { getInitials } from 'src/utils/get-initials';
import React, {useEffect, useState } from 'react';
import Link from 'next/link';
import EditIcon from '@mui/icons-material/Edit';
import { itemImage, rowsPerPageOptions, toTitleCase } from 'src/utils/util';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { CopyOutlined } from '@ant-design/icons';
import CommentIcon from '@mui/icons-material/Comment';
import {ItemReports} from 'src/sections/wholesale/item-reports';
import ReportGmailerrorredOutlinedIcon from '@mui/icons-material/ReportGmailerrorredOutlined';

export const ItemsTable = (props) => {
  const {
    count = 0,
    onDeselectAll,
    onDeselectOne,
    onPageChange = () => {},
    onRowsPerPageChange,
    onSelectAll,
    onSelectOne,
    page = 0,
    rowsPerPage = 0,
    selected = []
  } = props;
  const [items,setItems] = useState(props.items)
  const [message,setMessage] = useState("")
  const selectedSome = (selected.length > 0) && (selected.length < items.length);
  const selectedAll = (items.length > 0) && (selected.length === items.length);
  const [confirm,setConfirm] = useState(false)
  const [slug,setSlug] = useState(null)
  const [rowIndex,setRowIndex] = useState(-1)
  const [status,setStatus] = useState('')
  const [action,setAction] = useState('')
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
    // Asynchronously call copyTextToClipboard
      copyTextToClipboard(slug)
      .then(() => {
        // If successful, update the isCopied state value
        setItems((items).filter(customer => {
         if(customer.slug == slug){
          customer.isCopied = true
          setIsCopied(true);
         }
         return customer
      }))
        setTimeout(() => {
          setItems((items).filter(customer => {
            if(customer.slug == slug){
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

  useEffect(()=>{
    setItems(props.items)
  },[props.items])

  

  const handleClose =  () =>{
      setConfirm(false)
  }
  const confirmBox = () => {
    setConfirm(true)
  };

  const takeAction = (action) =>{
    if (action === 'delete'){
      props.onDelete(slug)
    }else if (action == 'status'){
      props.onStatusChange(slug,status)
    }else if (action == 'stock'){
      props.onChangeInStock(slug,status)
    }
    setConfirm(false)
  }

  return ( <>
    <Card sx={{overflowX: 'auto'}}>
        <Box sx={{ minWidth: 800}}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell padding="checkbox">
                </TableCell>
                <TableCell>
                  Name
                </TableCell>
                <TableCell>
                  Token
                </TableCell>

                <TableCell>
                  Label
                </TableCell>

                <TableCell>
                  Subcategory
                </TableCell>

                <TableCell>
                  Capacity/Weight
                </TableCell>

                <TableCell>
                  Rating
                </TableCell>
         
                <TableCell>
                 M.R.P
                </TableCell>

                <TableCell>
                  Discount
                </TableCell>

                <TableCell>
                  Discount%
                </TableCell>

                <TableCell>
                  Price
                </TableCell>

                <TableCell>
                  Total Comments
                </TableCell>

                <TableCell>
                  Total Reports
                </TableCell>

                <TableCell>
                  Stock
                </TableCell>

                <TableCell>
                  Status
                </TableCell>

                <TableCell>
                  Created at
                </TableCell>

                <TableCell>
                  ACTIONS
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {items.map((item,index) => {
                const isSelected = selected.includes(item.slug);
                const createdAt = format(item.createdAt, 'dd/MM/yyyy');

                return (
                  <TableRow
                    hover
                    key={item.id}
                    selected={isSelected}
                  >
                    <TableCell padding="checkbox">
                    </TableCell>
                    <TableCell>
                      <Stack
                        alignItems="center"
                        direction="row"
                        spacing={2}
                      >      
                 
                    {!!item.avtar ? <Image src={itemImage+item.slug+"/"+item.avtars?.split(',')[0]} style={{borderRadius : "50%" , width:"50px", height : "50px" }}/>  : 
                        <Avatar src={itemImage+item.slug+"/"+item.avtars?.split(',')[0]} >
                          {getInitials(item.name)}
                        </Avatar>
                        }
                   
                      <Typography variant="subtitle2">
                      {toTitleCase(item.name)}
                    </Typography>
                         
                      </Stack>
                    </TableCell>
               
       
                    <TableCell sx={{color:'text.secondary'}}>
                     <span style={{color:'green'}}>{item.slug} </span> 
                      {!!item.isCopied && item.isCopied && isCopied ? <Badge color="primary"  badgeContent="copied" style={{marginBottom:'35px'}} /> : <></>}
                      <CopyOutlined onClick={() => { handleCopyClick(item.slug) }} />
                    </TableCell>

                    <TableCell align='center'>
                        {item.label === "O" && <Badge color="error" badgeContent={'Old'} title="Old Items" />}
                        {item.label === "N" && <Badge color="success" badgeContent={'New'} title="New Items" />}
                    </TableCell>

                    <TableCell align='center'>
                        {toTitleCase(item.itemSubCategory.subcategory)}
                    </TableCell>

                    <TableCell align='center'>
                      {!!item.itemSubCategory.unit && item.itemSubCategory.unit != 'null' ? 
                        item.capacity + " " +item.itemSubCategory.unit : 'Not need to measure'
                      }
                    </TableCell>

                    <TableCell>
                      <Stack
                          alignItems="center"
                          direction="row"
                          spacing={2}
                        >
                          <Rating name="read-only" value={item.rating} readOnly titleAccess="Rating" />
                          <span title='Total Ratings'>{!!item.totalRatingCount ? item.totalRatingCount : 0}</span>
                        </Stack>
                    </TableCell>

                    <TableCell>
                    <Stack
                        alignItems="center"
                        direction="row"
                        spacing={2}
                      >
                        {item.price}
                      <CurrencyRupeeIcon sx={{fontSize:'15px',mt:'20px'}}/>
                      </Stack>
                    </TableCell>


                    <TableCell>
                    <Stack
                        alignItems="center"
                        direction="row"
                        spacing={2}
                      >
                            <span>{item.discount} </span>
                      <CurrencyRupeeIcon sx={{fontSize:'15px',mt:'20px'}}/>
                      <DiscountIcon sx={{color:'red',fontSize:'20px',mt:'20px',px:'0px'}} titleAccess='Discounts'/>

                      </Stack>
                    </TableCell>

                    <TableCell>
                    <Stack
                        alignItems="center"
                        direction="row"
                        spacing={2}
                      >
                         <span style={
                          {
                            color : 'green',
                            fontWeight : 'bold'
                          }}> {Math.floor((item.discount / item.price)*100 )+ "%"}</span>
                      </Stack>
                    </TableCell>

                    <TableCell>
                    <Stack
                        alignItems="center"
                        direction="row"
                        spacing={2}
                      >
                        {item.price-item.discount}
                      <CurrencyRupeeIcon sx={{fontSize:'15px',mt:'20px'}} titleAccess='Actual Prifce'/>
                      </Stack>
                    </TableCell>


                    {/* Total Comments */}
                    <TableCell>
                      <Stack
                          alignItems="center"
                          direction="row"
                          spacing={2}
                        >
                            <span>{!!item.totalComments ? item.totalComments : 0}</span>
                          <CommentIcon sx={{fontSize:'15px',mt:'20px'}} titleAccess='Comments'/>
                        </Stack>
                    </TableCell>
                    {/* Total reports */}
                    <TableCell>
                        <Stack
                              alignItems="center"
                              direction="row"
                              spacing={2}
                            >
                              <span> {!!item.totalReportsCount ? item.totalReportsCount : 0}</span>
                            <ReportGmailerrorredOutlinedIcon sx={{fontSize:'15px',mt:'20px',color : 'red'}} titleAccess='Reports'/>
                        </Stack>
                    </TableCell>


                    
                    <TableCell>
                     {item.inStock !== 'Y' ? <CancelIcon sx={ {
                        marginX : '2px',
                        color : 'Red'
                        
                        } }  titleAccess='In stock' onClick={(e)=> {
                          setMessage("We are going to add the item in stock.")
                          setSlug(item.slug)
                          setStatus('Y')
                          setAction('stock')
                          confirmBox()
                        }} />

                      : 
                      <CheckCircleIcon sx={ {
                        marginX : '2px',
                        color : 'Green'
                        
                        } } titleAccess='Out of stock' onClick={(e)=> {
                          setMessage("We are going to remove item from the stock.")
                          setSlug(item.slug)
                          setStatus('N')
                          setAction('stock')
                          confirmBox()
                        }} />
                      }
                    </TableCell>


                    <TableCell>
                      {/* {setStatus(item.status)} */}
                     {item.status !== 'A' ? <CancelIcon sx={ {
                        marginX : '2px',
                        color : 'Red'
                        
                        } }  titleAccess='Activate' onClick={(e)=> {
                          setMessage("We are going to activate this user.")
                          setSlug(item.slug)
                          setStatus('A')
                          setAction('status')
                          confirmBox()
                        }} />

                      : 
                      <CheckCircleIcon sx={ {
                        marginX : '2px',
                        color : 'Green'
                        
                        } } titleAccess='Deactivate' onClick={(e)=> {
                          setMessage("We are going to deactivate this user.")
                          setSlug(item.slug)
                          setStatus('D')
                          setAction('status')
                          confirmBox()
                        }} />
                      }
                    </TableCell>

                    <TableCell>
                      {createdAt}
                    </TableCell>

                    <TableCell>
                                        

                    <Link
                            href={{
                              pathname: '/item/comments/[slug]',
                              query: { slug: item.slug },
                            }}
                          >
                              <VisibilityIcon sx = {{
                                  marginX : '5px',
                                  color : '#111927'
                            }}
                            titleAccess='View Item Details'
                            />   
                      </Link>



                      <Link
                            href={{
                              pathname: '/item/update/[slug]',
                              query: { slug: item.slug },
                            }}
                          >
                              <EditIcon sx = {{
                                  marginX : '5px',
                                  color : '#111927'
                            }}
                            titleAccess='Edit'
                            />   
                      </Link>
                      <DeleteIcon sx={ {
                        marginX : '5px',
                        color : 'Red'
                        
                        } }  titleAccess='Delete' onClick={(e)=>{
                          setSlug(item.slug)
                          setRowIndex(index)
                          setMessage("We are going to delete this item if you agree press agree otherwise press disagree.")
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
          <Button onClick={()=>takeAction(action)} autoFocus>
            Agree
          </Button>
        </DialogActions>
      </Dialog>
    
    </>


  );
};

ItemsTable.propTypes = {
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
