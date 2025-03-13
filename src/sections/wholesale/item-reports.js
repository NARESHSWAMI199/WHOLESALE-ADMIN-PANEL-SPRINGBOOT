import {
    Avatar,
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
import { useEffect, useState } from 'react';
import { rowsPerPageOptions, toTitleCase, userImage } from 'src/utils/util';
import { getInitials } from 'src/utils/get-initials';

export const ItemReports = (props) => {
  const {
    count = 0,

    onPageChange = () => {},
    onRowsPerPageChange,
    page = 0,
    rowsPerPage = 0,
    selected = []
  } = props;
  const [itemReports,setItemReports] = useState(props.itemReports)
  const [message,setMessage] = useState("")
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

  

  const handleClose =  () =>{
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
                  Username
                </TableCell>
                <TableCell>
                  Category
                </TableCell>

                <TableCell>
                    Message
                </TableCell>

                <TableCell>
                  Reported At
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {itemReports.map((itemReport,index) => {
                const isSelected = selected.includes(itemReport.slug);
                const createdAt = format(itemReport.createdAt, 'dd/MM/yyyy');
                return (
                  <TableRow
                    hover
                    key={itemReport.id}
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
                 
                    {!!itemReport.user?.avtar ? <Image src={userImage+itemReport.user?.slug+"/"+item.avtar} style={{borderRadius : "50%" , width:"50px", height : "50px" }}/>  : 
                        <Avatar src={userImage+itemReport.user?.slug+"/"+itemReport.user?.avtar} >
                          {getInitials(itemReport.user?.username)}
                        </Avatar>
                        }
                   
                      <Typography variant="subtitle2">
                      {toTitleCase(itemReport.user?.username)}
                    </Typography>
                         
                      </Stack>
                    </TableCell>
               
       
                    <TableCell sx={{color:'text.secondary'}}>
                        {itemReport.category}
                    </TableCell>

                    <TableCell>
                        {itemReport.message}
                    </TableCell>

                    <TableCell>
                        {createdAt}
                    </TableCell>
                </TableRow>
              )})}
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

ItemReports.propTypes = {
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
