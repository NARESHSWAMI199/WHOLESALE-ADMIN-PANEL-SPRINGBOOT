import { useCallback, useEffect, useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Divider,
  TextField,
  Unstable_Grid2 as Grid,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';
export const AccountProfileDetails = (props) => {

  const [values,setValues] = useState(props.user)


  useEffect(()=>{
    setValues(props.user)
  },[props.user])

  const handleChange = useCallback(
    (event) => {
      setValues((prevState) => ({
        ...prevState,
        [event.target.name]: event.target.value
      }));
    },
    []
  );

  const handleSubmit = (event) => {
      event.preventDefault();
      let form = event.target
      let formData = new FormData(form)
      const data = {
        username : formData.get("username"),
        email :  formData.get("email"),
        contact : formData.get("contact"),
        userType : formData.get("userType")
      }
      props.updateProfile(data)
    }


  return (
    <form
      autoComplete="off"
      noValidate
      onSubmit={handleSubmit}
    >
      <Card>
        <CardHeader
          subheader="The information can be edited"
          title="Profile"
        />
        <CardContent sx={{ pt: 0 }}>
          <Box sx={{ m: -1.5 }}>
            <Grid
              container
              spacing={3}
            >
              <Grid
                xs={24}
                md={6}
              >
                <TextField
                  fullWidth
                  helperText="Please specify the first name"
                  label="Username"
                  name="username"
                  onChange={handleChange}
                  required
                  value={values.username || ''}
                />


              </Grid>
              <Grid
                xs={12}
                md={6}
              >
              {/* <InputLabel id="demo-simple-select-label">Age</InputLabel> */}
              <Select
                fullWidth
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                name='userType'
                value={values.userType ||''}
                label="User type"
                InputLabelProps={{ shrink: true }}
                onChange={handleChange}
              >
                <MenuItem value={"S"}>Staff</MenuItem>
                <MenuItem value={"W"}>Wholesaler</MenuItem>
                <MenuItem value={'R'}>Retailer</MenuItem>
              </Select>
              </Grid>
              <Grid
                xs={12}
                md={6}
              >
                <TextField
                  fullWidth
                  label="Email Address"
                  name="email"
                  onChange={handleChange}
                  required
                  value={values.email || ''}
                />
              </Grid>
              <Grid
                xs={12}
                md={6}
              >
                <TextField
                  fullWidth
                  label="Phone Number"
                  name="contact"
                  onChange={handleChange}
                  type="number"
                  value={values.contact || ''}
                />
              </Grid>
              {/* <Grid
                xs={12}
                md={6}
              >
                <TextField
                  fullWidth
                  label="Country"
                  name="country"
                  onChange={handleChange}
                  required
                  value={values.country}
                />
              </Grid>
              <Grid
                xs={12}
                md={6}
              >
                <TextField
                  fullWidth
                  label="Select State"
                  name="state"
                  onChange={handleChange}
                  required
                  select
                  SelectProps={{ native: true }}
                  value={values.state}
                >
                  {states.map((option) => (
                    <option
                      key={option.value}
                      value={option.value}
                    >
                      {option.label}
                    </option>
                  ))}
                </TextField>
              </Grid> */}
            </Grid>
          </Box>
        </CardContent>
        <Divider />
        <CardActions sx={{ justifyContent: 'flex-end' }}>
          <Button type="submit" variant="contained">
            Save details
          </Button>
        </CardActions>
      </Card>
    </form>
  );
};
