import PropTypes from 'prop-types';
import { format } from 'date-fns';
import DeleteIcon from '@mui/icons-material/Delete';
import CancelIcon from '@mui/icons-material/Cancel';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import {
  Avatar,
  Badge,
  Box,
  Card,
  Checkbox,
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

import { Scrollbar } from 'src/components/scrollbar';
import { getInitials } from 'src/utils/get-initials';
import React, {useEffect, useState } from 'react';
import Link from 'next/link';
import EditIcon from '@mui/icons-material/Edit';
import { host } from 'src/utils/util';
import { useAuth } from 'src/hooks/use-auth';
import axios from 'axios';
import { CopyOutlined } from '@ant-design/icons';



export const CategoryTable = (props) => {

  const [items,setItems] = useState(props.items)
  const [message,setMessage] = useState("")
  const [confirm,setConfirm] = useState(false)
  const [slug,setSlug] = useState(null)
  const [rowIndex,setRowIndex] = useState(-1)
  const [action,setAction] = useState('')
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('md'));
  const auth = useAuth()
  const user = auth.user  

  const handleClose =  () =>{
      setConfirm(false)
  }
  const confirmBox = () => {
    setConfirm(true)
  };

  const takeAction = (action) =>{
    props.onDelete(slug,rowIndex)
    setConfirm(false)
  }

  useEffect(()=>{
    setItems(props.items)
  },[props.items])

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
    

                <TableCell style={{textAlign:'right'}}>
                  ACTIONS
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {items.map((category,index) => {
                return (
                  <TableRow
                    hover
                    key={index}
             
                  >
                    <TableCell padding="checkbox">
                     
                    </TableCell>
                    <TableCell>

   

                      <Stack
                        alignItems="center"
                        direction="row"
                        spacing={2}
                      >      
                        <Avatar src={category.icon} >
                          {getInitials(category.category)}
                        </Avatar>
                        <Typography variant="subtitle2">
                          {category.category}
                        </Typography>
                      </Stack>
                    </TableCell>
    
                    <TableCell style={{textAlign:'right'}}> 
                      <Link
                            href={{
                              pathname: props.editUrl+'[categoryId]',
                              query: { categoryId: category.id},
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
                        
                        } }  titleAccess='delete' onClick={(e)=>{
                          setSlug(category.slug)
                          setRowIndex(index)
                          setMessage("We are going to delete this item category. if you agree press agree otherwise press disagree.")
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

CategoryTable.propTypes = {
  items: PropTypes.array,
};
