//
//  MealTableViewController.m
//  Storyboard Travel Notes
//
//  Created by Gianpaolo Caprara on 20/10/16.
//  Copyright © 2016 Gianpaolo Caprara. All rights reserved.
//

#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import "JournalsViewController.h"
#import "ServerCalls.h"
#import "Journals.h"


@interface JournalsViewController ()

@end

// MARK: Properties
NSMutableArray *journalsList;

@implementation JournalsViewController

- (void)viewDidLoad {
    self.navigationItem.title = @"Journals";
    [super viewDidLoad];
    if ([stringFromWall isEqualToString:@"NO JOURNALS SELECTED"]){
        self.navigationItem.hidesBackButton = YES;
    }
    // aggiungo il refresh alla view
    self.refreshControl = [[UIRefreshControl alloc] init];
    self.refreshControl.backgroundColor = [UIColor whiteColor];
    self.refreshControl.tintColor = [UIColor blackColor];
    [self.refreshControl addTarget:self action:@selector(loadJournals) forControlEvents:UIControlEventValueChanged];
    // carico i journal
    [self loadJournals];
    
}

// caricamento informazioni journals
-(void)loadJournals {
    
    ServerCalls* serverCalls = [ServerCalls getInstance];
    
    // converto lo UserID in un long long
    NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
    [f setNumberStyle:NSNumberFormatterDecimalStyle];
    NSNumber * fbIDValue = [f numberFromString:[FBSDKProfile currentProfile].userID];
    [serverCalls downloadAllJournals:^(NSMutableArray *journalsList) {
        Journals *journals = [Journals getInstance];
        [journals setJournalsInDict:journalsList];
        NSLog(@"Scaricamento completato");
    } andJournalOwnerID:[fbIDValue longLongValue]];

    Journals* journals = [Journals getInstance];
    journalsList = [journals getJournals];
    
    if (self.refreshControl){
        [self.tableView reloadData];
        [self.refreshControl endRefreshing];
    }
    
}

-(void) viewDidAppear:(BOOL)animated{
    self.tableView.tableFooterView = [[UIView alloc] initWithFrame:CGRectZero];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

// BEGIN : TABLE METHOD
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return journalsList.count;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    NSString *cellIdentifier = @"JournalTableViewCell";
    JournalTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier forIndexPath:indexPath];
    
    Journal *journal = journalsList[indexPath.row];
    cell.JournalName.text = journal.name;
    
    // recupero il nome dell'owner
    if ([FBSDKAccessToken currentAccessToken]) {
        [[[FBSDKGraphRequest alloc] initWithGraphPath:[NSString stringWithFormat:@"%llu" , journal.owner_id] parameters:@{@"fields": @"name"}]
         startWithCompletionHandler:^(FBSDKGraphRequestConnection *connection, id result, NSError *error) {
             if (!error) {
                 cell.JournalOwner.text = result[@"name"];
             }
         }];
    }
    cell.JournalDescription.text = journal.description;
    // formattazione data
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat: @"dd.MM.yyyy"];
    cell.DepartureDateJournal.text = [formatter stringFromDate:journal.departureDate];
    cell.ReturnDateJournal.text =  [formatter stringFromDate:journal.returnDate];
    
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    Journal *journalSelected = journalsList[indexPath.row];
    NSLog(@"Journal clicked %@", journalSelected.name);
    self->journalIDselected = [[JournalID alloc] initWithName:journalSelected.name andOwnerId:journalSelected.owner_id];
    
    // ritorno alla wall activity
    [self performSegueWithIdentifier:@"goBackToWallFromJournals" sender:nil];
    
}

// END: TABLE METHOD

-(void)reloadData{
    [self.tableView reloadData];
    
    if (self.refreshControl){
        [self.refreshControl endRefreshing];
    }
}

@end
