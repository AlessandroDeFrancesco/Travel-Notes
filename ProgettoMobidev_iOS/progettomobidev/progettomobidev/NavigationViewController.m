//
//  NavigationViewController.m
//  Travel Notes
//
//  Created by gianpaolo on 30/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "NavigationViewController.h"
#import "CreateJournalViewController.h"
#import "JournalsViewController.h"
#import "Journals.h"

@interface NavigationViewController ()

@end

@implementation NavigationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

// utilizzato per poter passare i valori all'altro VC prima di passare da un VC all'altro
- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if ([segue.identifier isEqualToString:@"fromWallToCreateJournal"]){
        CreateJournalViewController* journalVC = [segue destinationViewController];
        if ([[Journals getInstance] getJournals].count == 0){
            journalVC->stringFromWall = [[NSString alloc] initWithFormat: @"NO ACTIVE JOURNALS"];
        }
    } else if([segue.identifier isEqualToString:@"fromWallToJournals"]){
        JournalsViewController* journalsVC = [segue destinationViewController];
        if ([[Journals getInstance] getCurrentJournal] == nil){
            NSString *string = [[NSString alloc] initWithFormat:@"NO JOURNALS SELECTED"];
            journalsVC->stringFromWall = string;
        }
    }
    
}

@end
