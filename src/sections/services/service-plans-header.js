import ArrowDownOnSquareIcon from "@heroicons/react/24/solid/ArrowDownOnSquareIcon"
import ArrowUpOnSquareIcon from "@heroicons/react/24/solid/ArrowUpOnSquareIcon"
import PlusIcon from "@heroicons/react/24/solid/PlusIcon"
import { Box, Button, Container, Stack, SvgIcon, Typography } from "@mui/material"
import Link from "next/link"

const exp = require("constants")



export function ServicePlansHeaders(props){
    return (
        <>
        
        <Stack
              direction="row"
              justifyContent="space-between"
              spacing={4}
            >
              <Stack spacing={1}>
                <Typography variant="h4">
                  {props.headerTitle}
                </Typography>
              </Stack>
              <div>



              <Link
                    href={{
                      pathname:'/plan',
                    }}
                      >
                <Button
                  startIcon={(
                    <SvgIcon fontSize="small">
                      <PlusIcon />
                    </SvgIcon>
                  )}
                  variant="contained"
                >
                  Add
                </Button>
                </Link>
              </div>
            </Stack>
        </>
    )
}

