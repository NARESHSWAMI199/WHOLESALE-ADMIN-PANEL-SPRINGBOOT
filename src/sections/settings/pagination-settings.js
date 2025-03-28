import { Box, Button, Card, CardActions, CardContent, CardHeader, Divider, FormControl, Grid, InputLabel, MenuItem, Select, Stack, Typography } from '@mui/material'
import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { host, rowsPerPageOptions } from 'src/utils/util';
import { useAuth } from 'src/hooks/use-auth';



export const  PaginationSettings = (props) => {
    const [sortingLables, setSortingLabels] = useState([])
    const [rowsPerPageObj , setRowPerPageObj] = useState({})
    const auth = useAuth()

    useEffect(()=> {
        axios.defaults.headers = {
            Authorization: auth.token
          }
        // get all pagination setting labels
        axios.get(host + "/admin/pagination/all")
        .then(res => {
            let sortingLables = Object.values(res.data);  // there we get a object with key ; value where in value we all detail about keys and rowNumbers
            setSortingLabels(sortingLables)
            sortingLables.map(label => {
                let fieldFor = label.pagination?.fieldFor;
                rowsPerPageObj[fieldFor] = label.rowsNumber
                setRowPerPageObj({...rowsPerPageObj})
            })
        })
        .catch(err=>{
            props.showError(err);
        })
        
    },[])

    // TODO ; make sure call from auth-context or redux side 
    axios.defaults.headers = {
        Authorization: auth.token
      }
    const handleChange = (event,pagination) => {
        let rowsNumber = event.target.value;
        axios.post(host + "/admin/pagination/update", {
            paginationId : pagination.id,
            rowsNumber : rowsNumber
        })
        .then(res => {
            props.showSuccess(res.data.message)
        })
        .catch(err => {
            props.showError(err);
        })
        rowsPerPageObj[pagination?.fieldFor] = rowsNumber
        setRowPerPageObj({...rowsPerPageObj})
        // updated with redux also 
        auth.updatePaginations(rowsNumber,pagination)
    };

  return (<>
        <Card>
                <CardHeader
                subheader="Show per page rows"
                title="Rows per page"
                />
                <Divider />
                <CardContent>
                <Grid
                    container
                    spacing={6}
                    wrap="wrap"
                >
            
                    <Grid
                    item
                    md={8}
                    sm={6}
                    xs={12}
                    >
                        <Stack spacing={1}>
                        {sortingLables.map((label,key)=>{
                            let fieldFor = label.pagination?.fieldFor;
                            return (
                                <Box key={key} sx={{display  : 'flex', alignItems : 'center',justifyContent : 'center'}}>
                                        <Typography sx={{minWidth : 150}} variant="h6">
                                            {label.pagination?.fieldFor}
                                        </Typography>

                                        <FormControl fullWidth sx={{mx : 3}}> 
                                            <Select
                                                labelId="demo-simple-select-label"
                                                id="demo-simple-select"
                                                value={rowsPerPageObj[fieldFor]}
                                                onChange={(e) => handleChange(e,label.pagination)}
                                            >
                                                {rowsPerPageOptions.map((value , index)=>{
                                                    return <MenuItem key={index} value={value}>{value}</MenuItem>
                                                })}

                                            </Select>
                                        </FormControl>
                                </Box>
                              
                         
                              
                            )
                        })}
                        </Stack>
                    </Grid>
                </Grid>
                </CardContent>
            </Card>
</>
  )
}
