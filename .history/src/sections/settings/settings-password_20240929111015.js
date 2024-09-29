import { useCallback, useState } from 'react';
import {
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Divider,
  Stack,
  TextField
} from '@mui/material';
import { useAuth } from 'src/hooks/use-auth';


export const SettingsPassword = () => {
  const [values, setValues] = useState({
    password: '',
    confirm: ''
  });


  const auth = useAuth()
  const user = auth.user;

  const handleChange = useCallback(
    (event) => {
      setValues((prevState) => ({
        ...prevState,
        [event.target.name]: event.target.value
      }));
    },
    []
  );

  const handleSubmit = useCallback(
    (event) => {
      event.preventDefault();
      const form = e.target;
      const formData = new FormData(form)
      let password = formData.get("password");
      let confirm = formData.get("confirm");

      if(password !== confirm) {
        setMessage("Confirm password doesn't watch.")
        return false
      }
      let item = {
        slug: user.slug,
        password: formData.get("password")
      }

      axios.defaults.headers = {
        Authorization: auth.token
      }
      axios.post(host + "/admin/auth/password", item)
        .then(res => {
          setMessage(res.data.message)
          setFlag("success")
          form.reset();
        }).catch(err => {
          setMessage(!!err.response ? err.response.data.message : err.message)
          setFlag("error")
        })
      setOpen(true)
    },[])


  return (
    <form onSubmit={handleSubmit}>
      <Card>
        <CardHeader
          subheader="Update password"
          title="Password"
        />
        <Divider />
        <CardContent>
          <Stack
            spacing={3}
            sx={{ maxWidth: 400 }}
          >
            <TextField
              fullWidth
              label="Password"
              name="password"
              onChange={handleChange}
              type="password"
              value={values.password}
            />
            <TextField
              fullWidth
              label="Password (Confirm)"
              name="confirm"
              onChange={handleChange}
              type="password"
              value={values.confirm}
            />
          </Stack>
        </CardContent>
        <Divider />
        <CardActions sx={{ justifyContent: 'flex-end' }}>
          <Button variant="contained">
            Update
          </Button>
        </CardActions>
      </Card>
    </form>
  );
};
