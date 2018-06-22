//
//  InformationJournalViewController.m
//  Travel Notes
//
//  Created by gianpaolo on 30/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import <FBSDKCoreKit/FBSDKCoreKit.h>

#import "InformationJournalViewController.h"
#import "Journals.h"
#import "Journal.h"
#import "JournalID.h"
#import "FacebookFriend.h"
#import "FriendsTableViewCell.h"
#import "FriendsAlreadyAddedTableViewCell.h"
#import "Utility.h"
#import "FacebookUtility.h"

@interface InformationJournalViewController (){
    Journal *_actualJournal;
    NSArray *_pickerTypes;
    NSMutableArray *_friendsListAlreadyAdded;
    NSMutableArray *_friendsListStillNotAdd;
    NSMutableArray *_participants;
}

@end

@implementation InformationJournalViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.navigationItem.title = @"Info Journal";

    // rendo le celle non editabili
    self.journalName.userInteractionEnabled = NO;
    self.journalDescription.userInteractionEnabled = NO;
    self.textPlace.userInteractionEnabled = NO;
    self.journalTypePicker.userInteractionEnabled = NO;

    // prendo il journal corrente
    _actualJournal = [[Journals getInstance] getCurrentJournal];
    
    // recupero i partecipanti del journal
    _participants = [[NSMutableArray alloc] initWithArray:_actualJournal.participants];
    [_participants addObject:@(_actualJournal.owner_id)];
    
    _pickerTypes = @[_actualJournal.type];

    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.dateStyle = NSDateFormatterShortStyle;
    // setto le celle del journal scelto
    self.journalName.text = _actualJournal.name;
    self.journalDescription.text = _actualJournal.description;
    self.textPlace.text = _actualJournal.city;
    self.journalTypePicker.delegate = self;
    self.journalTypePicker.dataSource = self;
    self.textDeparture.text = [dateFormatter stringFromDate:_actualJournal.departureDate];
    self.textReturn.text = [dateFormatter stringFromDate:_actualJournal.returnDate];

    // per la lista degli amici già aggiunti al journal (compreso l'owner ID)
    self.facebookFriendsAlreadyAddedTable.delegate = self;
    self.facebookFriendsAlreadyAddedTable.dataSource = self;
    self.facebookFriendsAlreadyAddedTable.allowsSelection = NO;
    self.facebookFriendsAlreadyAddedTable.scrollEnabled = YES;
    //[self getFriendsListAlreadyAdded];
    
    // per la lista degli amici che si possono aggiungere (se sei l'owner)
    if (_actualJournal.owner_id == [[Utility getCurrentUserFacebookID] longLongValue]){
        self.facebookFriendsNotAddedTable.delegate = self;
        self.facebookFriendsNotAddedTable.dataSource = self;
        self.facebookFriendsNotAddedTable.scrollEnabled = YES;
        //[self loadNewFriends];
    }

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

-(void)viewDidAppear:(BOOL)animated{    
    if (_actualJournal.owner_id != [[Utility getCurrentUserFacebookID] longLongValue]){
        self.tableFriendsStillNotAdded.hidden =YES;
        self.buttonUpdate.hidden = YES;
        // aggiusto la view
        [self.view setFrame:CGRectMake(self.view.frame.origin.x, self.view.frame.origin.x, self.view.frame.size.width, 950)];
    } else{
        [_facebookFriendsNotAddedTable reloadData];
    }

}

- (void) viewWillAppear:(BOOL)animated{
    [super viewWillAppear:YES];
    [self getFriendsListAlreadyAdded];
    // per la lista degli amici che si possono aggiungere (se sei l'owner)
    if (_actualJournal.owner_id == [[Utility getCurrentUserFacebookID] longLongValue]){
        [self loadNewFriends];
    }
}


// verifica quali amici sono aggiunti al journal
-(void) getFriendsListAlreadyAdded{
    _friendsListAlreadyAdded = [[NSMutableArray alloc] init];
    for (id element in _participants){
        // converto lo UserID in un long long
        NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
        [f setNumberStyle:NSNumberFormatterDecimalStyle];
        NSString *userID = element;
        [[FacebookUtility getInstance] getNameFromID:[userID longLongValue] andFacebookNameCallback:^(NSString *name) {
            [_friendsListAlreadyAdded addObject:name];
            dispatch_async(dispatch_get_main_queue(), ^{
                [_facebookFriendsAlreadyAddedTable reloadData];
            });
        }];
    }
}

// verifica chi non è ancora aggiunto al journal e ne segnala la possibilita'
-(void) loadNewFriends{
    _friendsListStillNotAdd = [[NSMutableArray alloc] init];
    if ([FBSDKAccessToken currentAccessToken]) {
        [[[FBSDKGraphRequest alloc] initWithGraphPath:[NSString stringWithFormat:@"/%@/friends", [[FBSDKProfile currentProfile] userID]] parameters:@{@"fields": @"name, id"}]
         startWithCompletionHandler:^(FBSDKGraphRequestConnection *connection, id result, NSError *error) {
             if (!error) {
                 for (NSDictionary *element in result[@"data"]){
                     // converto lo UserID in un long long
                     NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
                     [f setNumberStyle:NSNumberFormatterDecimalStyle];
                     NSNumber * fbIDValue = [f numberFromString:element[@"id"]];
                     if (![_participants containsObject:@([fbIDValue longLongValue])]){
                         FacebookFriend *newFacebookFriend = [[FacebookFriend alloc] initWithName:element[@"name"] andIdFriend: element[@"id"]];
                         [_friendsListStillNotAdd addObject:newFacebookFriend];
                     }
                 }
                 dispatch_async(dispatch_get_main_queue(), ^{
                     [_facebookFriendsNotAddedTable reloadData];
                 });
             }
         }];
    }
}

// BEGIN: PICKER METHOD
// The number of columns of data
- (int)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

// The number of rows of data
- (int)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return _pickerTypes.count;
}

// The data to return for the row and component (column) that's being passed in
- (NSString*)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return _pickerTypes[row];
}
// END: PICKER METHOD

// BEGIN: TABLE METHOD

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (tableView == self.facebookFriendsAlreadyAddedTable){
        return _friendsListAlreadyAdded.count;
    } else if (tableView == self.facebookFriendsNotAddedTable){
        return _friendsListStillNotAdd.count;
    } else {
        return 0;
    }
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    if (tableView == self.facebookFriendsAlreadyAddedTable){
        NSString *cellIdentifier = @"FriendFacebookAlreadyAddedCell";
        FriendsAlreadyAddedTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier forIndexPath:indexPath];
        
        NSString *friendName = _friendsListAlreadyAdded[indexPath.row];
        cell.friendsName.text = friendName;
        
        return cell;
    } else if (tableView == self.facebookFriendsNotAddedTable){
        NSString *cellIdentifier = @"FriendFacebookCell";
        FriendsTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier forIndexPath:indexPath];
        
        FacebookFriend *friend = _friendsListStillNotAdd[indexPath.row];
        NSString *name = friend.name;
        cell.friendName.text = name;
        
        return cell;
    } else {
        return nil;
    }
    
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    if (tableView == self.facebookFriendsNotAddedTable){
        FriendsTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
        if(!cell.check){
            [[cell checkBox] setImage:[UIImage imageNamed:@"CheckBox Marked"] forState:UIControlStateNormal];
            cell.check = YES;
            ((FacebookFriend*)_friendsListStillNotAdd[indexPath.row]).checked = YES;
        } else if (cell.check) {
            [[cell checkBox] setImage:[UIImage imageNamed:@"CheckBox Empty"] forState:UIControlStateNormal];
            cell.check = NO;
            ((FacebookFriend*)_friendsListStillNotAdd[indexPath.row]).checked = NO;
        }
    } else if (tableView == self.facebookFriendsAlreadyAddedTable){

    }
    
}
// END :TABLE METHOD

- (IBAction)updateJournal:(id)sender {
    // aggiorno il journal
    if ([Utility isNetworkAvailable]){
        NSLog(@"Update journal %@" , _actualJournal.name);
        
        // converto lo UserID in un long long
        NSNumberFormatter * f = [[NSNumberFormatter alloc] init];
        [f setNumberStyle:NSNumberFormatterDecimalStyle];
        
        // aggiungo i partecipanti
        for(int i = 0; i < _friendsListStillNotAdd.count;i++){
            FacebookFriend *friend = [FacebookFriend alloc];
            friend = _friendsListStillNotAdd[i];
            if (friend.checked){
                NSNumber * fbIDValue = [f numberFromString:friend.idFriend];
                [_actualJournal addParticipant:fbIDValue];
            }
        }
        
        self->journalIDUpdated = [[Journals getInstance] updateJournal:_actualJournal];
        NSLog(@"Aggiornamento effettuato %@", journalIDUpdated);

        // ritorno alla wall activity
        [self performSegueWithIdentifier:@"goToWallFromInformationJournal" sender:nil];
    } else {
        NSLog(@"CONNECTION NOT AVAILABLE");
        // visualizzo un messaggio di errore
        NSString *message = @"Impossible to complete the operation. Connection is not available. Please, turn on internet and retry the operation";
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                                       message:message
                                                                preferredStyle:UIAlertControllerStyleAlert];
        
        [self presentViewController:alert animated:YES completion:nil];
        
        int duration = 3; // duration in seconds
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, duration * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
            [alert dismissViewControllerAnimated:YES completion:nil];
        });
    }
    
}

- (IBAction)inviteFriends:(id)sender {
    FBSDKAppInviteContent *content =[[FBSDKAppInviteContent alloc] init];
    content.appLinkURL = [NSURL URLWithString:@"https://www.travelnotes.com/travelnotes"];
    //optionally set previewImageURL
    content.appInvitePreviewImageURL = [NSURL URLWithString:@"https://www.travelnotes.com/travelnotes.jpg"];
    
    // Present the dialog. Assumes self is a view controller
    // which implements the protocol FBSDKAppInviteDialogDelegate.
    [FBSDKAppInviteDialog showFromViewController:self
                                     withContent:content
                                        delegate:self];
}

- (void)appInviteDialog:(FBSDKAppInviteDialog *)appInviteDialog didCompleteWithResults:	(NSDictionary *)results{
    NSLog(@"Invites sended");
}

- (void)appInviteDialog:(FBSDKAppInviteDialog *)appInviteDialog didFailWithError:(NSError *)error {
    NSLog(@"Error with invites");
}


@end
