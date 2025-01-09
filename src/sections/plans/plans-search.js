import MagnifyingGlassIcon from '@heroicons/react/24/solid/MagnifyingGlassIcon';
import { RefreshOutlined, SearchOutlined } from '@mui/icons-material';
import KeyIcon from '@mui/icons-material/Key';
import { Button, Card, Grid, InputAdornment, MenuItem, OutlinedInput, Select, SvgIcon, TextField } from '@mui/material';
import { format } from 'date-fns';
import { useState } from 'react';
export const PlanSearch = (props) => {

  const previousDate = format(new Date().getTime()-(10 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd')
  const currentDate = format(new Date().getTime()+(24 * 60 * 60 * 1000), 'yyyy-MM-dd')


  

    const [values,setValues] = useState({
      inStock : 'Y',
      status : 'A',
      type : 'A',
      fromDate : previousDate,
      toDate : currentDate
    })

    const handleChange = (e) =>{
      setValues({
        ...values,
        [e.target.name] : [e.target.value]
      })
    }

  const handleSubmit = (e)=>{
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form)
    const data = {
      searchKey : formData.get("searchKey"),
      fromDate : new Date(formData.get("fromDate")).getTime(),
      toDate : new Date(formData.get("toDate")).getTime(),
      slug : formData.get("slug"),
      status :  formData.get("status"),
      userType : formData.get("type") !== "A" ? formData.get("type") : null,
      inStock :  formData.get("inStock")
    }
    props.onSearch(data);
  }
  

  const resetFilters = (e) => {
    /** reset default filters  */
    setValues({  inStock : 'Y',
      status : 'A',
      type : 'A',
      fromDate : previousDate,
      toDate : currentDate
    })
    props.onSearch();
  }
  

 return (<Card sx={{ p: 2 }}>
    <form onSubmit={(e)=>{handleSubmit(e)}}>
    <Grid container spacing={1}>
    <Grid item xs={12} md={2}>
      <OutlinedInput
            onChange={handleChange}
            value={values.slug}
            fullWidth
            placeholder="Token Id"
            name='slug'
            sx={{height:55}}
            startAdornment={(
              <InputAdornment position="start" >
                
                <KeyIcon
                  color="action"
                  fontSize="small"
                >
                  <MagnifyingGlassIcon />
                </KeyIcon>
              </InputAdornment>
            )}
          />
          </Grid>


        <Grid item xs={12} md={2}>
          <Select
            fullWidth
            sx={{height:55}}
            labelId="demo-simple-select-label"
            id="demo-simple-select"
            name='status'
            onChange={handleChange}
            value={values.status}
            label="Status"
          >
            <MenuItem value={"A"}>Active</MenuItem>
            <MenuItem value={"D"}>Expired</MenuItem>
          </Select>
          </Grid>


      <Grid item xs={12} md={2}>      
        <TextField
          sx={{height:55}}
          fullWidth
          id="datetime-local"
          label="Created From Date"
          type="date"
          name='createdFromDate'
          onChange={handleChange}
          value={values.fromDate}
          // defaultValue={previousDate}
          InputLabelProps={{
            shrink: true,
          }}
        />
        </Grid>

      <Grid item xs={12} md={2}>  
        <TextField
            sx={{height:55}}
            fullWidth
            id="datetime-local"
            label="Created To Date"
            type="date"
            // defaultValue={currentDate}
            onChange={handleChange}
            value={values.toDate}
            name='createdToDate'
            InputLabelProps={{
              shrink: true,
            }}
          />
      </Grid>




      <Grid item xs={12} md={2}>      
        <TextField
          sx={{height:55}}
          fullWidth
          id="datetime-local"
          label="Expried From Date"
          type="date"
          name='expiredFromDate'
          onChange={handleChange}
          value={values.fromDate}
          // defaultValue={previousDate}
          InputLabelProps={{
            shrink: true,
          }}
        />
        </Grid>

      <Grid item xs={12} md={2}>  
        <TextField
            sx={{height:55}}
            fullWidth
            id="datetime-local"
            label="Expried To Date"
            type="date"
            // defaultValue={currentDate}
            onChange={handleChange}
            value={values.toDate}
            name='expiredToDate'
            InputLabelProps={{
              shrink: true,
            }}
          />
      </Grid>



      <Grid item xs={6} md={1}>  
        <Button type='submit'
          sx={{height:55, width : '100%'}}
          startIcon={(
                <SvgIcon fontSize="small">
                  <SearchOutlined />
                </SvgIcon>
              )}
              variant="contained"> Search 
        </Button>
      </Grid>

      <Grid item xs={6} md={1}>
        <Button type='reset' onClick={resetFilters} sx={{height : 54,width: '100%',background:'red',mx:1}} 
          startIcon={(
              <SvgIcon fontSize="small">
                <RefreshOutlined />
              </SvgIcon>
            )}
            variant="contained"> Reset 
          </Button>
        </Grid>

    </Grid>
    </form>
  </Card>
)};
