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
  kanbanBoard: {
    margin: theme.spacing(6, 0, 3),
    minHeight: '500px',
  },
  kanbanColumn: {
    height: '100%',
  },
  kanbanCard: {
    margin: theme.spacing(1),
  },
  expand: {
    transform: 'rotate(0deg)',
    transition: theme.transitions.create('transform', {
      duration: theme.transitions.duration.shortest,
    }),
  },
  expandOpen: {
    transform: 'rotate(180deg)',
    transition: theme.transitions.create('transform', {
      duration: theme.transitions.duration.shortest,
    }),
  },
  cardActionButton: {
    marginLeft: 'auto',
  },
  avatar: {
    backgroundColor: colors.red[500],
  },
}));

// util function for synchronising rest data
function useFetch(url, updateTrigger) {
  const [data, setData] = React.useState([]);

  React.useEffect(() => {
    fetch(url)
      .then(response => response.json())
      .then(newData => setData(newData));
  }, [updateTrigger]);
  return data;
}

function sendHttpPost(url, json, onCompletion) {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(json)
    };
    fetch(url, requestOptions)
     .then(response => (response.status >= 400) ? alert(`Failed: Response ${response.status} ${response.statusText}`) : "")
     .then(onCompletion());
}

function KanbanBoard(props) {
  const classes = useStyles();
  const tickets = props.tickets;
  const postData = props.postData;

  const startTicket = (id, assignee) => { postData(`/ticket/${id}/start`, { "assignee": assignee}); }
  const assignTicket = (id, assignee) => { postData(`/ticket/${id}/assign`, { "assignee": assignee}); }
  const completeTicket = (id) => { postData(`/ticket/${id}/complete`, {}); }

  return (
    <Grid container spacing={3} className={classes.kanbanBoard} direction="row" justify="center" alignItems="stretch">
        <KanbanColumn name="Backlog" startTicket={startTicket} cardData={tickets.filter(it => it.kanban_column == "Backlog")} />
        <KanbanColumn name="In Development" assignTicket={assignTicket} completeTicket={completeTicket} cardData={tickets.filter(it => it.kanban_column == "InDevelopment")} />
        <KanbanColumn name="Completed" cardData={tickets.filter(it => it.kanban_column == "Done")} />
    </Grid>
    );
}

function KanbanColumn(props) {
    const classes = useStyles();
    const name = props.name;
    const startTicket = props.startTicket;
    const assignTicket = props.assignTicket;
    const completeTicket = props.completeTicket;
    return (
        <Grid item xs={12} md={4}>
            <Paper elevation={3} className={classes.kanbanColumn}>
                <Typography variant="h6" component="h6">{name}</Typography>
                {props.cardData.map(card => (
                   <KanbanCard key={card.id} card={card} startTicket={startTicket} assignTicket={assignTicket} completeTicket={completeTicket} />
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

    const startTicket = props.startTicket;
    const [startDialogOpen, setStartDialogOpen] = React.useState(false);
    const toggleStartDialogOpen = () => { setStartDialogOpen(!startDialogOpen); }

    const assignTicket = props.assignTicket;
    const [assignDialogOpen, setAssignDialogOpen] = React.useState(false);
    const toggleAssignDialogOpen = () => { setAssignDialogOpen(!assignDialogOpen); }

    const completeTicket = props.completeTicket;
    const handleDone = () => { completeTicket(card.id); }

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
                    <IconButton
                        onClick={handleExpandClick}
                        aria-expanded={expanded}
                        className={expandMoreClassName}
                        aria-label="show more">
                        <Icon>expand_more</Icon>
                    </IconButton>
                }
            />
            <CardActions disableSpacing>
                {startTicket && (
                    <Button size="small" color="primary" className={classes.cardActionButton} onClick={toggleStartDialogOpen}>Start</Button>
                )}
                {assignTicket && (
                    <Button size="small" color="primary" className={classes.cardActionButton} onClick={toggleAssignDialogOpen}>Assign</Button>
                )}
                {completeTicket && (
                    <Button size="small" color="primary" className={classes.cardActionButton} onClick={handleDone}>Done</Button>
                )}
            </CardActions>
            <Collapse in={expanded} timeout="auto" unmountOnExit>
                <CardContent>
                   <Typography variant="overline">ID</Typography>
                   <Typography paragraph>{card.id}</Typography>
                   <Typography variant="overline">Description</Typography>
                   <Typography paragraph>{card.description}</Typography>
                   { card.assignee && (
                       <div>
                           <Typography variant="overline">Assigned to</Typography>
                          <Avatar>{card.assignee.charAt(0).toUpperCase()}</Avatar>
                           <Typography paragraph>{card.assignee}</Typography>
                       </div>
                   )}
                </CardContent>
            </Collapse>
            {startTicket && (
                <AssignTicketDialog isOpen={startDialogOpen} id={card.id} assignTicket={startTicket} onClose={toggleStartDialogOpen} />
            )}
            {assignTicket && (
                <AssignTicketDialog isOpen={assignDialogOpen} id={card.id} assignTicket={assignTicket} onClose={toggleAssignDialogOpen} />
            )}
        </Card>
    );
}

function AssignTicketDialog(props) {
  const classes = useStyles();
  const isOpen = props.isOpen;
  const handleClose = props.onClose;
  const handleAssignTicket = props.assignTicket;
  const handleSave = (event) => {
    event.preventDefault();
    const form = event.target;
    handleAssignTicket(props.id, form.assignee.value);
    handleClose();
  };
  return (
     <div>
      <Dialog open={isOpen} onClose={handleClose} aria-labelledby="assign-ticket-title">
        <DialogTitle id="assign-ticket-title">Assign Ticket</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Please assign the ticket to a user.
          </DialogContentText>
          <form id="assign_ticket_form" onSubmit = {handleSave}>
              <TextField
                required
                autoFocus
                margin="dense"
                id="assignee"
                label="Assign To User"
                type="text"
                fullWidth
              />
          </form>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">Cancel</Button>
          <Button color="primary" type="submit" form="assign_ticket_form">Save</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

function NewTicketDialog(props) {
  const classes = useStyles();
  const isOpen = props.isOpen;
  const handleClose = props.handleClose;
  const postData = props.postData;

  const handleSave = (event) => {
    event.preventDefault();
    const form = event.target;
    postData("/ticket", { "title": form.title.value, "description": form.description.value});
    handleClose();
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
  const postData = props.postData;
  const [isOpen, setOpen] = React.useState(false);
  const toggleOpen = () => { setOpen(!isOpen); };
  return (
    <div>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" className={classes.title}>
              Helpdesk Tickets
          </Typography>
          <IconButton color="inherit" aria-label="new item" onClick={toggleOpen}>
              <Icon>add</Icon>
          </IconButton>
        </Toolbar>
      </AppBar>

      <NewTicketDialog handleClose={toggleOpen} postData={postData} isOpen={isOpen} />
    </div>
  );
}

function App() {
  const [lastUpdateCount, setLastUpdateCount] = React.useState(0);
  const incrementLastUpdateCount = () => { setLastUpdateCount(lastUpdateCount + 1) }
  const tickets = useFetch('http://localhost:8080/tickets', lastUpdateCount);
  const postData = (uriPath, json) => {
    sendHttpPost('http://localhost:8080' + uriPath, json, incrementLastUpdateCount);
  }
  return (
    <Container maxWidth="lg">
      <TopNavBar postData={postData} />
      <KanbanBoard postData={postData} tickets={tickets} />
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