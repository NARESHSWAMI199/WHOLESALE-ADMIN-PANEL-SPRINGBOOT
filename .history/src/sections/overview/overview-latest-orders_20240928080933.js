import { format } from 'date-fns';
import PropTypes from 'prop-types';
import ArrowRightIcon from '@heroicons/react/24/solid/ArrowRightIcon';
import {
  Avatar,
  Box,
  Button,
  Card,
  CardActions,
  CardHeader,
  Divider,
  Link,
  Stack,
  SvgIcon,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow
} from '@mui/material';
import { Scrollbar } from 'src/components/scrollbar';
import { SeverityPill } from 'src/components/severity-pill';
import { toTitleCase } from 'src/utils/util';

const statusMap = {
  warning: 'warning',
  success: 'success',
  error: 'error'
};

export const OverviewLatestUsers = (props) => {
  const { users = [], sx } = props;

  return (
    <Card sx={sx}>
      <CardHeader title="Latest Orders" />
      <Scrollbar sx={{ flexGrow: 1 }}>
        <Box sx={{ minWidth: 800 }}>
          <Table>
            <TableHead>
              <TableRow>
                {/* <TableCell>
                  Order
                </TableCell> */}
                <TableCell>
                  Username
                </TableCell>
                <TableCell>
                  User Type
                </TableCell>
                <TableCell sortDirection="desc">
                  Date
                </TableCell>
                <TableCell>
                  Status
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {users.map((user) => {
                const createdAt = format(user.createdAt, 'dd/MM/yyyy');

                return (
                  <TableRow
                    hover
                    key={user.slug}
                  >
                    {/* <TableCell>
                      {order.ref}
                    </TableCell> */}
                    <TableCell>
                      {/* {toTitleCase(user.username)} */}
                      <Stack
                        alignItems="center"
                        direction="row"
                        spacing={2}
                      >
                        <Link
                          href={{
                            pathname: '/account/[slug]',
                            query: { slug: user.slug },
                          }}
                        >
                          <Avatar src={host + "/admin/auth/profile/" + user.avatar} >
                            {getInitials(customer.username)}
                          </Avatar>
                        </Link>
                        <Typography variant="subtitle2">
                          {toTitleCase(customer.username)}
                        </Typography>
                      </Stack>
                    </TableCell>
                    <TableCell>
                      {user.userType =="W" && "Wholesaler"}
                      {user.userType == "S" && "Staff"}
                      {user.userType == "R" && "Retailer"}
                    </TableCell>
                    <TableCell>
                      {createdAt}
                    </TableCell>
                    <TableCell>
                      <SeverityPill color={statusMap[user.status == "A" ? "success" : "error"]}>
                        {user.status == "A" ? "Active" : "Deactive"}
                      </SeverityPill>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </Box>
      </Scrollbar>
      <Divider />
      <CardActions sx={{ justifyContent: 'flex-end' }}>
        <Link href="/users">
        <Button
          color="inherit"
          endIcon={(
            <SvgIcon fontSize="small">
              <ArrowRightIcon />
            </SvgIcon>
          )}
          size="small"
          variant="text"
        >
          View all
        </Button>
        </Link>
      </CardActions>
    </Card>
  );
};

OverviewLatestUsers.prototype = {
  orders: PropTypes.array,
  sx: PropTypes.object
};
