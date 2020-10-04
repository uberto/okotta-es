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
      .then(data => setData(data));
  }, [updateTrigger]);
  return data;
}

function postData(url, json) {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(json)
    };
    fetch(url, requestOptions)
     .then(response => (response.status >= 400) ? alert(`Failed: Response ${response.status} ${response.statusText}`) : "");
}

function KanbanBoard(props) {
  const tickets = props.tickets;
  const startTicket = props.startTicket;
  const completeTicket = props.completeTicket;
  const classes = useStyles();
  return (
    <Grid container spacing={3} className={classes.kanbanBoard} direction="row" justify="center" alignItems="stretch">
        <KanbanColumn name="Backlog" startTicket={startTicket} cardData={tickets.filter(it => it.kanban_column == "Backlog")} />
        <KanbanColumn name="In Development" completeTicket={completeTicket} cardData={tickets.filter(it => it.kanban_column == "InDevelopment")} />
        <KanbanColumn name="Completed" cardData={tickets.filter(it => it.kanban_column == "Done")} />
    </Grid>
    );
}

function KanbanColumn(props) {
    const classes = useStyles();
    const name = props.name;
    const startTicket = props.startTicket;
    const completeTicket = props.completeTicket;
    return (
        <Grid item xs={12} md={4}>
            <Paper elevation={3} className={classes.kanbanColumn}>
                <Typography variant="h6" component="h6">{name}</Typography>
                {props.cardData.map(card => (
                   <KanbanCard key={card.id} card={card} startTicket={startTicket} completeTicket={completeTicket} />
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
    const toggleDialogOpen = () => { setStartDialogOpen(!startDialogOpen); }

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
                    <Button size="small" color="primary" className={classes.cardActionButton} onClick={toggleDialogOpen}>Start</Button>
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
                <StartTicketDialog isOpen={startDialogOpen} id={card.id} startTicket={startTicket} onClose={toggleDialogOpen} />
            )}
        </Card>
    );
}

function StartTicketDialog(props) {
  const classes = useStyles();
  const isOpen = props.isOpen;
  const handleClose = props.onClose;
  const handleStartTicket = props.startTicket;
  const handleSave = (event) => {
    event.preventDefault();
    const form = event.target;
    handleStartTicket(props.id, form.assignee.value);
    return handleClose();
  };
  return (
     <div>
      <Dialog open={isOpen} onClose={handleClose} aria-labelledby="start-ticket-title">
        <DialogTitle id="start-ticket-title">Start Ticket</DialogTitle>
        <DialogContent>
          <DialogContentText>
            To move the ticket to In Progress please assign the ticket to a user.
          </DialogContentText>
          <form id="new_ticket_form" onSubmit = {handleSave}>
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
          <Button color="primary" type="submit" form="new_ticket_form">Save</Button>
        </DialogActions>
      </Dialog>
    </div>
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
  const toggleOpen = () => { setOpen(!isOpen); };
  const handleAddTicket = props.addTicket;

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
      <NewTicketDialog
         handleClose={toggleOpen}
         handleAddTicket={handleAddTicket}
         isOpen={isOpen}
      />
    </div>
  );
}

function App() {
  const [lastUpdateCount, setLastUpdateCount] = React.useState(0);
  const addTicket = (title, description) => {
      postData("http://localhost:8080/ticket", { "title": title, "description": description});
      setLastUpdateCount(lastUpdateCount + 1);
  }
  const startTicket = (id, assignee) => {
      postData(`http://localhost:8080/ticket/${id}/start`, { "assignee": assignee});
      setLastUpdateCount(lastUpdateCount + 1);
  }
  const completeTicket = (id) => {
      postData(`http://localhost:8080/ticket/${id}/complete`, {});
      setLastUpdateCount(lastUpdateCount + 1);
  }
  const tickets = useFetch('http://localhost:8080/tickets', lastUpdateCount);

  return (
    <Container maxWidth="lg">
      <TopNavBar addTicket={addTicket} />
      <KanbanBoard tickets={tickets} startTicket={startTicket} completeTicket={completeTicket} />
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