import PropTypes from 'prop-types';
import { Avatar, Card, CardContent, Stack, SvgIcon, Typography } from '@mui/material';
import { PeopleOutline } from '@mui/icons-material';
import LocalGroceryStoreIcon from '@mui/icons-material/LocalGroceryStore';
import StoreIcon from '@mui/icons-material/Store';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import { useEffect, useMemo, useState } from 'react';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
export const OverviewUsers = (props) => {
  const { sx, title } = props;
  const [value, setValue] = useState(props.value)
  const [tag,setTag] = useState('success.main')


  useEffect(()=>{
    setValue(props.value)
  },[props.value])



  useEffect(()=>{
    if(title === "RETAILERS") {
      setTag('error.main')
    }else if(title === "WHOLESALERS") {
      setTag('success.main')
    }else if(title === "STAFFS") {
      setTag('info.main')
    }else{
      setTag('primary.main')
    }
  },[tag])


 
  return (
    <Card sx={sx}>
      <CardContent>
        <Stack
          alignItems="flex-start"
          direction="row"
          justifyContent="space-between"
          spacing={3}
        >
          <Stack spacing={1}>
            <Typography
              color="text.secondary"
              variant="overline"
            >
              {props.title}
            </Typography>
            <Typography variant="h4">
              {value.all}
            </Typography>
          </Stack>
          <Avatar
              sx={{
                backgroundColor : tag,
                height: 56,
                width: 56
              }}
            > 
              <SvgIcon>
              {title==="WHOLESALERS" &&
                <StoreIcon />
              }
              {title==="USERS" &&
                <PeopleOutline />
              }
              {title==="RETAILERS" &&
                <LocalGroceryStoreIcon />
              }
              {title==="STAFFS" &&
                <VerifiedUserIcon />
              }

              {title==="ADMINS" &&
                <AdminPanelSettingsIcon />
              }

              </SvgIcon>
          </Avatar>
        </Stack>


        <Stack
              alignItems="RIGHT"
              direction="row"
              spacing={1}
              sx={{ mt: 2 }}
            >
              <SvgIcon
                color='success'
                fontSize="small"
              >
                <PeopleOutline />
              </SvgIcon>
              <Typography
                color='success.main'
                variant="body2"
                sx={{
                  fontWeight:'bold'
                }}
              >
                {value.active}
              </Typography>
              <Typography
                color='text.secondary'
                variant="caption"
              >
                ACTIVE
              </Typography>
            </Stack>

          <Stack
            alignItems="left"
            direction="row"
            spacing={1}
            sx={{ mt: 0.5}}
          >
                <SvgIcon
                color='error'
                fontSize="small"
              >
                <PeopleOutline />
              </SvgIcon>
              <Typography
                color='error.main'
                variant="body2"
                sx={{
                  fontWeight:'bold'
                }}
              >
                {value.deactive}
              </Typography>
              <Typography
                color='text.secondary'
                variant="caption"
              >
                DEACTIVE
              </Typography>
          </Stack>
      </CardContent>
    </Card>
  );
};

OverviewUsers.prototypes = {
  sx: PropTypes.object,
  value: PropTypes.string.isRequired
};
