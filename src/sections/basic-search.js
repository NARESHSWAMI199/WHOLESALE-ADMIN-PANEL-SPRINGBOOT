import MagnifyingGlassIcon from '@heroicons/react/24/solid/MagnifyingGlassIcon';
import { SearchOutlined,RefreshOutlined } from '@mui/icons-material';
import { Button, Card, Grid, InputAdornment, MenuItem, OutlinedInput, Select, SvgIcon, TextField } from '@mui/material';
import { format } from 'date-fns';
import { useCallback, useState } from 'react';
import KeyIcon from '@mui/icons-material/Key';
import { width } from '@mui/system';
export const BasicSearch = (props) => {

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
        value={values.searchKey}
        fullWidth
        placeholder="Search"
        name='searchKey'
        sx={{height:55}}
        startAdornment={(
          <InputAdornment position="start" >
            
            <SvgIcon
              color="action"
              fontSize="small"
            >
              <MagnifyingGlassIcon />
            </SvgIcon>
          </InputAdornment>
        )}
      />
      </Grid>
   

      {/* { props.type !== "A" &&  props.type !== "item" && */}
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
          {/* } */}



        { props.type === "A" && 
            <Grid item xs={12} md={2}>
            <Select
              sx={{height:55}}
              fullWidth
              labelId="demo-simple-select-label"
              id="demo-simple-select"
              name='type'
              onChange={handleChange}
              value={values.type}
              label="User type"
            >
              <MenuItem value={"A"}>All</MenuItem>
              <MenuItem value={"S"}>Staff</MenuItem>
              <MenuItem value={"W"}>Wholesaler</MenuItem>
              <MenuItem value={'R'}>Retailer</MenuItem>
            </Select>
            </Grid>}

          {props.type !== "G" && 
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
            <MenuItem value={"D"}>Deactive</MenuItem>
          </Select>
          </Grid>}


        {props.type == "item" &&   
          <Grid item xs={12} md={2}><Select
          sx={{height:55}}
          fullWidth
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          name='inStock'
          onChange={handleChange}
          value={values.inStock}
          label="Status"
        >
          <MenuItem value={"Y"}>In stock</MenuItem>
          <MenuItem value={"N"}>Out of stock</MenuItem>
        </Select></Grid>}

      <Grid item xs={12} md={2}>      
        <TextField
          sx={{height:55}}
          fullWidth
          id="datetime-local"
          label="From Date"
          type="date"
          name='fromDate'
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
            label="To Date"
            type="date"
            // defaultValue={currentDate}
            onChange={handleChange}
            value={values.toDate}
            name='toDate'
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
