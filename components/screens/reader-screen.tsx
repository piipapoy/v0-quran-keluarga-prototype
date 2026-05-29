'use client'

import { useState } from 'react'
import { ChevronLeft, Bookmark, Share2, Users, ChevronUp, ChevronDown } from 'lucide-react'
import { useApp } from '@/lib/app-context'
import { Button } from '@/components/ui/button'
import { BottomNav } from '@/components/bottom-nav'
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetDescription,
} from '@/components/ui/sheet'

// Placeholder Arabic text - NOT real Quran verses
const PLACEHOLDER_VERSES = [
  {
    number: 43,
    arabic: 'هٰذَا نَصٌّ عَرَبِيٌّ لِلْعَرْضِ فَقَطْ وَلَيْسَ آيَةً قُرْآنِيَّةً',
    translation: '[Ini adalah teks placeholder Arab untuk tampilan saja, bukan ayat Al-Quran yang sebenarnya]',
  },
  {
    number: 44,
    arabic: 'هٰذَا مِثَالٌ آخَرُ لِنَصٍّ عَرَبِيٍّ لِأَغْرَاضِ التَّصْمِيمِ',
    translation: '[Contoh lain teks placeholder Arab untuk keperluan desain UI]',
  },
  {
    number: 45,
    arabic: 'نَصٌّ عَرَبِيٌّ ثَالِثٌ يُسْتَخْدَمُ لِتَوْضِيحِ التَّخْطِيطِ',
    translation: '[Teks placeholder ketiga yang digunakan untuk menunjukkan tata letak aplikasi]',
    isCurrentBookmark: true,
  },
  {
    number: 46,
    arabic: 'هٰذَا النَّصُّ لِلْعَرْضِ التَّوْضِيحِيِّ فَحَسْبُ',
    translation: '[Teks ini hanya untuk tujuan demonstrasi tampilan]',
  },
  {
    number: 47,
    arabic: 'نَصٌّ عَرَبِيٌّ نَمُوذَجِيٌّ لِتَصْمِيمِ الْوَاجِهَةِ',
    translation: '[Teks placeholder Arab untuk desain antarmuka pengguna]',
  },
]

export function ReaderScreen() {
  const { state, setScreen, updateFamilyProgress, setPersonalBookmark } = useApp()
  const [showUpdateSheet, setShowUpdateSheet] = useState(false)
  const [showSuccessToast, setShowSuccessToast] = useState(false)
  const [selectedVerse, setSelectedVerse] = useState<number>(state.currentAyat)
  const [currentVerseIndex, setCurrentVerseIndex] = useState(2) // Index of ayat 45

  const handleUpdateProgress = () => {
    updateFamilyProgress(state.currentSurah, selectedVerse)
    setShowUpdateSheet(false)
    setShowSuccessToast(true)
    setTimeout(() => setShowSuccessToast(false), 3000)
  }

  const handlePersonalBookmark = () => {
    setPersonalBookmark(state.currentSurah, selectedVerse)
    setShowSuccessToast(true)
    setTimeout(() => setShowSuccessToast(false), 3000)
  }

  const navigateVerse = (direction: 'up' | 'down') => {
    if (direction === 'up' && currentVerseIndex > 0) {
      setCurrentVerseIndex(prev => prev - 1)
      setSelectedVerse(PLACEHOLDER_VERSES[currentVerseIndex - 1].number)
    } else if (direction === 'down' && currentVerseIndex < PLACEHOLDER_VERSES.length - 1) {
      setCurrentVerseIndex(prev => prev + 1)
      setSelectedVerse(PLACEHOLDER_VERSES[currentVerseIndex + 1].number)
    }
  }

  return (
    <div className="min-h-screen bg-background flex flex-col">
      {/* Header */}
      <header className="sticky top-0 z-40 bg-card/95 backdrop-blur-md border-b border-border/50 px-4 py-3 safe-area-top">
        <div className="flex items-center justify-between">
          <button
            onClick={() => setScreen('home')}
            className="w-10 h-10 rounded-full flex items-center justify-center hover:bg-secondary transition-colors"
          >
            <ChevronLeft className="w-5 h-5 text-foreground" />
          </button>
          <div className="text-center">
            <h1 className="font-semibold text-foreground">{state.currentSurah}</h1>
            <p className="text-xs text-muted-foreground">Ayat {PLACEHOLDER_VERSES[0].number} - {PLACEHOLDER_VERSES[PLACEHOLDER_VERSES.length - 1].number}</p>
          </div>
          <div className="w-10" /> {/* Spacer */}
        </div>
      </header>

      {/* Verse Navigation */}
      <div className="fixed right-3 top-1/2 -translate-y-1/2 z-30 flex flex-col gap-2">
        <button
          onClick={() => navigateVerse('up')}
          disabled={currentVerseIndex === 0}
          className="w-10 h-10 rounded-full bg-card border border-border shadow-lg flex items-center justify-center disabled:opacity-30 transition-opacity"
        >
          <ChevronUp className="w-5 h-5 text-foreground" />
        </button>
        <button
          onClick={() => navigateVerse('down')}
          disabled={currentVerseIndex === PLACEHOLDER_VERSES.length - 1}
          className="w-10 h-10 rounded-full bg-card border border-border shadow-lg flex items-center justify-center disabled:opacity-30 transition-opacity"
        >
          <ChevronDown className="w-5 h-5 text-foreground" />
        </button>
      </div>

      {/* Reader Content */}
      <main className="flex-1 px-5 py-6 pb-36 overflow-y-auto">
        <div className="max-w-lg mx-auto space-y-8">
          {/* Disclaimer */}
          <div className="text-center p-3 rounded-xl bg-accent/20 border border-accent/30">
            <p className="text-xs text-muted-foreground">
              ⚠️ Teks Arab di bawah ini adalah <strong>placeholder</strong>, bukan ayat Al-Quran yang sebenarnya.
            </p>
          </div>

          {PLACEHOLDER_VERSES.map((verse, index) => (
            <article
              key={verse.number}
              className={`relative ${index === currentVerseIndex ? 'opacity-100' : 'opacity-40'} transition-opacity duration-300`}
            >
              {/* Family bookmark indicator */}
              {verse.isCurrentBookmark && (
                <div className="absolute -left-3 top-0 bottom-0 w-1 rounded-full bg-primary" />
              )}
              
              {/* Verse number badge */}
              <div className="flex justify-center mb-4">
                <span className={`inline-flex items-center justify-center w-10 h-10 rounded-full text-sm font-medium ${
                  verse.isCurrentBookmark
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-secondary text-foreground'
                }`}>
                  {verse.number}
                </span>
              </div>

              {/* Arabic text */}
              <p
                className="arabic-text text-foreground mb-4 font-serif"
                style={{ fontSize: `${state.fontSize}px` }}
              >
                {verse.arabic}
              </p>

              {/* Translation */}
              {state.showTranslation && (
                <p className="text-sm text-muted-foreground leading-relaxed text-center">
                  {verse.translation}
                </p>
              )}

              {/* Family bookmark badge */}
              {verse.isCurrentBookmark && (
                <div className="flex justify-center mt-4">
                  <span className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-primary/10 text-primary text-xs font-medium">
                    <Users className="w-3 h-3" />
                    Bookmark Keluarga
                  </span>
                </div>
              )}
            </article>
          ))}
        </div>
      </main>

      {/* Action Bar */}
      <div className="fixed bottom-20 left-0 right-0 z-40 px-4 pb-4">
        <div className="max-w-md mx-auto bg-card/95 backdrop-blur-md rounded-2xl border border-border shadow-lg p-2 flex items-center justify-around">
          <ActionButton
            icon={<Bookmark className="w-5 h-5" />}
            label="Bookmark"
            onClick={handlePersonalBookmark}
          />
          <ActionButton
            icon={<Users className="w-5 h-5" />}
            label="Update"
            onClick={() => {
              setSelectedVerse(PLACEHOLDER_VERSES[currentVerseIndex].number)
              setShowUpdateSheet(true)
            }}
            primary
          />
          <ActionButton
            icon={<Share2 className="w-5 h-5" />}
            label="Bagikan"
            onClick={() => {}}
          />
        </div>
      </div>

      {/* Update Progress Sheet */}
      <Sheet open={showUpdateSheet} onOpenChange={setShowUpdateSheet}>
        <SheetContent side="bottom" className="rounded-t-3xl">
          <SheetHeader className="text-center pb-4">
            <SheetTitle className="text-xl font-semibold">
              Update Progress Keluarga
            </SheetTitle>
            <SheetDescription asChild>
              <div className="space-y-4 pt-2">
                <div className="p-4 rounded-2xl bg-secondary">
                  <p className="text-lg font-medium text-foreground">
                    {state.currentSurah} ayat {selectedVerse}
                  </p>
                </div>
                <div className="flex items-center gap-2 justify-center text-amber-600 bg-amber-50 dark:bg-amber-950/30 p-3 rounded-xl">
                  <Users className="w-4 h-4" />
                  <p className="text-sm">
                    Semua anggota keluarga akan melihat progress terbaru.
                  </p>
                </div>
              </div>
            </SheetDescription>
          </SheetHeader>
          <div className="flex gap-3 pt-4">
            <Button
              variant="outline"
              onClick={() => setShowUpdateSheet(false)}
              className="flex-1 h-12 rounded-xl"
            >
              Batal
            </Button>
            <Button
              onClick={handleUpdateProgress}
              className="flex-1 h-12 rounded-xl bg-primary text-primary-foreground"
            >
              Update Progress
            </Button>
          </div>
        </SheetContent>
      </Sheet>

      {/* Success Toast */}
      {showSuccessToast && (
        <div className="fixed top-20 left-1/2 -translate-x-1/2 z-50 animate-in fade-in slide-in-from-top-2 duration-300">
          <div className="bg-primary text-primary-foreground px-4 py-2 rounded-full shadow-lg text-sm font-medium">
            ✓ Berhasil disimpan
          </div>
        </div>
      )}

      <BottomNav />
    </div>
  )
}

function ActionButton({
  icon,
  label,
  onClick,
  primary,
}: {
  icon: React.ReactNode
  label: string
  onClick: () => void
  primary?: boolean
}) {
  return (
    <button
      onClick={onClick}
      className={`flex flex-col items-center gap-1 px-4 py-2 rounded-xl transition-colors ${
        primary
          ? 'bg-primary text-primary-foreground'
          : 'text-foreground hover:bg-secondary'
      }`}
    >
      {icon}
      <span className="text-[10px] font-medium">{label}</span>
    </button>
  )
}
