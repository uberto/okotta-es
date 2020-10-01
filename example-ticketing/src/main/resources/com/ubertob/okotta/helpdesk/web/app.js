const {
  colors,
  CssBaseline,
  ThemeProvider,
  Typography,
  Container,
  Collapse,
  makeStyles,
  createMuiTheme,
  Dialog,
  DialogTitle,
  DialogContentText,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Grid,
  Box,
  Paper,
  Card,
  CardHeader,
  CardActions,
  CardContent,
  Avatar,
  IconButton,
  Icon,
  AppBar,
  Toolbar,
} = MaterialUI;

// Create a theme instance.
const theme = createMuiTheme({
  palette: {
    primary: {
      main: '#556cd6',
    },
    secondary: {
      main: '#19857b',
    },
    error: {
      main: colors.red.A400,
    },
    background: {
      default: '#fff',
    },
  },
});

const useStyles = makeStyles(theme => ({
  root: {
    margin: theme.spacing(6, 0, 3),
  },
  title: {
    flexGrow: 1,
  },
  kanbanCard: {
    margin: theme.spacing(1),
  },
  expand: {
    transform: 'rotate(0deg)',
    marginLeft: 'auto',
    transition: theme.transitions.create('transform', {
      duration: theme.transitions.duration.shortest,
    }),
  },
  expandOpen: {
    marginLeft: 'auto',
    transform: 'rotate(180deg)',
    transition: theme.transitions.create('transform', {
      duration: theme.transitions.duration.shortest,
    }),
  },
  avatar: {
    backgroundColor: colors.red[500],
  },
}));

function KanbanBoard() {
  const [ tickets, setTickets ] = React.useState(
  [
    //TODO: replace these with REST API data from helpdesk app
    {
        "title": "App crashes when clicking mouse on icon",
        "description": "Customer was unable to reproduce this error and it only happened once",
        "assignee": "Fred",
        "kanban_column": "Done"
    },
    {
        "title": "Cancel button doesn't work properly",
        "description": "On the shopping cart page clicking the cancel button continues to checkout instead of closing the page",
        "assignee": null,
        "kanban_column": "Backlog"
    },
    {
        "title": "Customer login issue",
        "description": "Bob from Brentford was unable to login to his account this morning and would like a password reset",
        "assignee": null,
        "kanban_column": "Backlog"
    }
  ] );
  const classes = useStyles();
  return (
    <Grid container spacing={3} className={classes.root} direction="row" justify="center" alignItems="stretch">
        <KanbanColumn name="Backlog" cardData={tickets.filter(it => it.kanban_column == "Backlog")} />
        <KanbanColumn name="In Development" cardData={tickets.filter(it => it.kanban_column == "InDevelopment")} />
        <KanbanColumn name="Completed" cardData={tickets.filter(it => it.kanban_column == "Done")} />
    </Grid>
    );
}

function KanbanColumn(props) {
    return (
        <Grid item xs={4}>
            <Paper elevation={2}>
                <Typography variant="h6" component="h6">{props.name}</Typography>
                {props.cardData.map(card => (
                   <KanbanCard card={card}/>
                ))}
            </Paper>
        </Grid>
    );
}

function KanbanCard(props) {
    const classes = useStyles();
    const [expanded, setExpanded] = React.useState(false);
    const handleExpandClick = () => { setExpanded(!expanded); };
    const expandMoreClassName = expanded ? classes.expandOpen : classes.expand;
    const card = props.card;
    return (
        <Card className={classes.kanbanCard}>
            <CardHeader
                title={card.title}
                subheader={""}
                avatar={
                  <Avatar aria-label="assignee" className={card.assignee != null ? classes.avatar : null}>
                    {card.assignee != null ? card.assignee.charAt(0).toUpperCase() : "-"}
                  </Avatar>
                }
                action={
                  <IconButton aria-label="settings">
                    <Icon>more_vert</Icon>
                  </IconButton>
                }
            />
            <CardActions disableSpacing>
                <IconButton aria-label="move to next">
                    <Icon>switch_right</Icon>
                </IconButton>
                <IconButton
                    onClick={handleExpandClick}
                    aria-expanded={expanded}
                    className={expandMoreClassName}
                    aria-label="show more">
                    <Icon>expand_more</Icon>
                </IconButton>
            </CardActions>
            <Collapse in={expanded} timeout="auto" unmountOnExit>
                <CardContent>
                   <Typography paragraph>{card.description}</Typography>
                </CardContent>
            </Collapse>
        </Card>
    );
}

function NewTicketDialog(props) {
  const classes = useStyles();
  const handleClose = props.handleClose;
  const open = props.open;
  return (
     <div>
      <Dialog open={open} onClose={handleClose} aria-labelledby="new-ticket-title">
        <DialogTitle id="new-ticket-title">New Ticket</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Create a new entry in the backlog.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            id="title"
            label="Title"
            type="text"
            fullWidth
          />
          <TextField
            autoFocus
            margin="dense"
            id="description"
            label="Description"
            type="text"
            fullWidth
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Cancel
          </Button>
          <Button onClick={handleClose} color="primary">
            Save
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

function TopNavBar() {
  const classes = useStyles();

  const [open, setOpen] = React.useState(false);
  const handleClickOpen = () => { setOpen(true); };
  const handleClose = () => { setOpen(false); };

  return (
    <div>
      <AppBar position="static">
        <Toolbar>
          <IconButton edge="start" color="inherit" aria-label="menu">
            <Icon>menu</Icon>
          </IconButton>
          <Typography variant="h6" className={classes.title}>
            Helpdesk Tickets
          </Typography>
          <div>
              <IconButton color="inherit" aria-label="new item" onClick={handleClickOpen}>
                <Icon>add</Icon>
              </IconButton>
              <NewTicketDialog handleClose={handleClose} open={open}/>
          </div>
        </Toolbar>
      </AppBar>
    </div>
  );
}

function App() {
  return (
    <Container maxWidth="lg">
      <TopNavBar/>
      <KanbanBoard/>
    </Container>
  );
}

ReactDOM.render(
  <ThemeProvider theme={theme}>
    {/* CssBaseline kickstart an elegant, consistent, and simple baseline to build upon. */}
    <CssBaseline />
    <App />
  </ThemeProvider>,
  document.getElementById('app')
);