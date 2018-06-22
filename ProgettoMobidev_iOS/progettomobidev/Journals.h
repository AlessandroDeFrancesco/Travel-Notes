//
//  Journals.h
//  Travel Notes
//
//  Created by gianpaolo on 07/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#ifndef Journals_h
#define Journals_h
#import "JournalID.h"
#import "Journal.h"

#import <Foundation/Foundation.h>

@interface Journals : NSObject {
    NSMutableDictionary* journals;
    JournalID* currentJournalID;
}

@property (nonatomic,strong) NSMutableDictionary* journals;
@property (nonatomic,strong) JournalID* currentJournalID;

+ (Journals*) getInstance;
- (void) setJournalsInDict: (NSMutableArray*) newJournals;
- (JournalID*) addJournal: (Journal*) journal;
- (Journal*) getJournal: (JournalID*) journal_id;
- (NSMutableArray*) getJournals;
- (void) setCurrentJournal: (JournalID*) journal_id;
- (Journal*) getCurrentJournal;
- (JournalID*) updateJournal: (Journal*) journal;

@end
#endif /* Journals_h */
