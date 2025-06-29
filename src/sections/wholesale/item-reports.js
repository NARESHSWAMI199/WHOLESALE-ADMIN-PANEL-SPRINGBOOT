import { Image } from '@mui/icons-material';
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
import { useState } from 'react';
import { getInitials } from 'src/utils/get-initials';
import { rowsPerPageOptions, toTitleCase, userImage } from 'src/utils/util';

export const ItemReports = (props) => {
  const {
    count = 0,

    onPageChange = () => {},
    onRowsPerPageChange,
    page = 0,
    rowsPerPage = 0,
    selected = []
  } = props;
  const [itemReports] = useState(props.itemReports)
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
              {itemReports.length ?  itemReports.map((itemReport,index) => {
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
                        {itemReport?.reportCategory?.categoryTitle}
                    </TableCell>

                    <TableCell>
                        {itemReport.message}
                    </TableCell>

                    <TableCell>
                        {createdAt}
                    </TableCell>
                </TableRow>
              )})
            :
            <TableRow>
              <TableCell colSpan={5} align="center">
                No Item Reports Found
              </TableCell>
            </TableRow>
            }
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
