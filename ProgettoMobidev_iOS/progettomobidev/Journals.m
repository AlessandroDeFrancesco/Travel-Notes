//
//  Journals.m
//  Travel Notes
//
//  Created by gianpaolo on 07/10/16.
//  Copyright © 2016 capraraedefrancescosoft. All rights reserved.
//

#import "Journals.h"

@implementation Journals

@synthesize journals,currentJournalID;

#pragma mark - singleton method

+ (Journals*)getInstance
{
    static Journals *sharedJournals = nil;
    @synchronized(self) {
        if (sharedJournals == nil)
            sharedJournals = [[self alloc] init];
    }
    return sharedJournals;
}

- (Journals*)init{
    if (self = [super init]){
        journals = [NSMutableDictionary dictionary];
        currentJournalID = [[JournalID alloc] init];
    }
    return self;
}

- (void)dealloc {
    // Should never be called, but just here for clarity really.
}

- (void) setJournalsInDict: (NSMutableArray*) newJournals{
    journals = [NSMutableDictionary dictionary];
    // per ogni journal parte la sottoiscrizione al topic del journal
    // e viene aggiunto all'hashmap
    for (Journal* journal in newJournals){
        [journals setObject:journal forKey: (id <NSCopying>)[[JournalID alloc] initWithName:journal.name andOwnerId:journal.owner_id]];
    }
}

- (JournalID*) addJournal: (Journal*) journal{
    JournalID* newID = [[JournalID alloc] initWithName:journal.name andOwnerId:journal.owner_id];
    [journals setObject:journal forKey: (id <NSCopying>)newID];
    
    return newID;
}

- (Journal*) getJournal: (JournalID*) journal_id{
    return [journals objectForKey:(id <NSCopying>)journal_id];
}

- (NSMutableArray*) getJournals{
    NSMutableArray* returnArray = [NSMutableArray arrayWithArray: [journals allValues]];
    return returnArray;
}

- (void) setCurrentJournal: (JournalID*) journal_id{
    currentJournalID = journal_id;
}

- (Journal*) getCurrentJournal{
    return [journals objectForKey:(id <NSCopying>)currentJournalID];
}

- (JournalID*) updateJournal: (Journal*) journal{
    JournalID* newID = [[JournalID alloc] initWithName:journal.name andOwnerId:journal.owner_id];
    [journals setObject:journal forKey: (id <NSCopying>)newID];
    /*Journal precedente = journals.put(newID, newJournal);
     
     if(precedente == null){
     Log.e("TravelNotes", "Errore: updateJournal non ha trovato il journal che doveva rimpiazzare");
     }*/
    
    return newID;
}

@end
