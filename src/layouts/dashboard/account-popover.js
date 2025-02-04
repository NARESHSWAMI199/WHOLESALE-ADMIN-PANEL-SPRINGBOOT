import { UserOutlined } from '@ant-design/icons';
import UserIcon from '@heroicons/react/24/solid/UserIcon';
import { LogoutOutlined } from '@mui/icons-material';
import { Box, Divider, MenuItem, MenuList, Popover, SvgIcon, Typography } from '@mui/material';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import PropTypes from 'prop-types';
import { useCallback, useEffect, useState } from 'react';
import { useAuth } from 'src/hooks/use-auth';

export const AccountPopover = (props) => {
  const { anchorEl, onClose, open } = props;
  const router = useRouter();
  const auth = useAuth();
  const [user,setUser] = useState(props.user)


  useEffect(()=>{
    setUser(props.user)
  },[props.user])

  const handleSignOut = useCallback(
    () => {
      onClose?.();
      auth.signOut();
      router.push('/auth/login');
    },
    [onClose, auth, router]
  );


  return (
    <Popover
      anchorEl={anchorEl}
      anchorOrigin={{
        horizontal: 'left',
        vertical: 'bottom'
      }}
      onClose={onClose}
      open={open}
      PaperProps={{ sx: { width: 200 } }}
    >
      <Box
        sx={{
          py: 1.5,
          px: 2
        }}
      >
        <Typography variant="overline">
          Account
        </Typography>
        <Typography
          color="text.secondary"
          variant="body2"
        >
          <Link style={{
              display : 'flex',
              alignItems : 'center',
              textDecoration:'none',
              color:'black'
            }} 
            href={{
              pathname : "/account"
            }}> 
            <UserOutlined style={{marginRight : 5}} title='Edit profile' />
            {!!user.username ? (user.username).toUpperCase() : ""}
          </Link>
     
        </Typography>
      </Box>
      <Divider />
      <MenuList
        disablePadding
        dense
        sx={{
          p: '8px',
          '& > *': {
            borderRadius: 1
          }
        }}
      >
        <MenuItem onClick={handleSignOut}>
          <LogoutOutlined fontSize='35' style={{marginRight : 5}}/>
          Sign out
        </MenuItem>
      </MenuList>
    </Popover>
  );
};

AccountPopover.propTypes = {
  anchorEl: PropTypes.any,
  onClose: PropTypes.func,
  open: PropTypes.bool.isRequired
};
