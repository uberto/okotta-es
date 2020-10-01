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

// util function for synchronising rest data
function useFetch(url) {
  const [data, setData] = React.useState([]);

  React.useEffect(() => {
    fetch(url)
      .then(response => response.json())
      .then(data => setData(data));
  }, []);

  return data;
}

function postData(url, json) {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(json)
    };
    fetch(url, requestOptions)
     .then(response => (response.status >= 400) ? alert("Failed: Response {response.status} {response.statusText}") : "");
}

function KanbanBoard(props) {
  const tickets = props.tickets;
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
  const isOpen = props.isOpen;
  const handleClose = props.handleClose;
  const handleAddTicket = props.handleAddTicket;
  const handleSave = (event) => {
    event.preventDefault();
    const form = event.target;
    handleAddTicket(form.title.value, form.description.value);
    return handleClose();
  };
  return (
     <div>
      <Dialog open={isOpen} onClose={handleClose} aria-labelledby="new-ticket-title">
        <DialogTitle id="new-ticket-title">New Ticket</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Create a new entry in the backlog.
          </DialogContentText>
          <form id="new_ticket_form" onSubmit = {handleSave}>
              <TextField
                required
                autoFocus
                margin="dense"
                id="title"
                label="Title"
                type="text"
                fullWidth
              />
              <TextField
                required
                autoFocus
                margin="dense"
                id="description"
                label="Description"
                type="text"
                fullWidth
              />
          </form>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">Cancel</Button>
          <Button color="primary" type="submit" form="new_ticket_form">Save</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

function TopNavBar(props) {
  const classes = useStyles();

  const [isOpen, setOpen] = React.useState(false);
  const handleClickOpen = () => { setOpen(true); };
  const handleClose = () => { setOpen(false); };
  const handleAddTicket = props.addTicket;

  return (
    <div>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" className={classes.title}>
              Helpdesk Tickets
          </Typography>
          <IconButton color="inherit" aria-label="new item" onClick={handleClickOpen}>
              <Icon>add</Icon>
          </IconButton>
        </Toolbar>
      </AppBar>
      <NewTicketDialog
         handleClose={handleClose}
         handleAddTicket={handleAddTicket}
         isOpen={isOpen}
      />
    </div>
  );
}

function App() {
  const tickets = useFetch('http://localhost:8080/tickets');
  const addTicket = (title, description) => {
      postData("http://localhost:8080/ticket", { "title": title, "description": description})
  }
  return (
    <Container maxWidth="lg">
      <TopNavBar addTicket={addTicket} />
      <KanbanBoard tickets={tickets} />
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